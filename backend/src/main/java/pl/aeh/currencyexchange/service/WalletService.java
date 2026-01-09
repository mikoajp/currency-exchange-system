package pl.aeh.currencyexchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.aeh.currencyexchange.dto.WalletDto;
import pl.aeh.currencyexchange.exception.ResourceNotFoundException;
import pl.aeh.currencyexchange.model.Transaction;
import pl.aeh.currencyexchange.model.TransactionStatus;
import pl.aeh.currencyexchange.model.TransactionType;
import pl.aeh.currencyexchange.model.User;
import pl.aeh.currencyexchange.model.Wallet;
import pl.aeh.currencyexchange.repository.TransactionRepository;
import pl.aeh.currencyexchange.repository.UserRepository;
import pl.aeh.currencyexchange.repository.WalletRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public List<WalletDto> getUserWallets(Long userId) {
        return walletRepository.findAllByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public WalletDto getOrCreateWallet(Long userId, String currency) {
        return walletRepository.findByUserIdAndCurrency(userId, currency)
                .map(this::mapToDto)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    
                    Wallet wallet = Wallet.builder()
                            .user(user)
                            .currency(currency)
                            .balance(BigDecimal.ZERO)
                            .build();
                    
                    return mapToDto(walletRepository.save(wallet));
                });
    }

    @Transactional
    public WalletDto depositPLN(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        // Use lock to prevent concurrent modification issues
        Wallet wallet = walletRepository.findByUserIdAndCurrencyWithLock(userId, "PLN")
                .orElseThrow(() -> new ResourceNotFoundException("PLN wallet not found. Please contact support."));

        wallet.deposit(amount);
        Wallet savedWallet = walletRepository.save(wallet);

        // Record transaction
        Transaction transaction = Transaction.builder()
                .user(wallet.getUser())
                .type(TransactionType.DEPOSIT)
                .toCurrency("PLN")
                .toAmount(amount)
                .status(TransactionStatus.COMPLETED)
                .build();
        
        transactionRepository.save(transaction);
        log.info("Deposit of {} PLN for user {} completed", amount, userId);

        return mapToDto(savedWallet);
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long userId, String currency) {
        return walletRepository.getBalanceByUserIdAndCurrency(userId, currency)
                .orElse(BigDecimal.ZERO);
    }

    private WalletDto mapToDto(Wallet wallet) {
        return WalletDto.builder()
                .id(wallet.getId())
                .currency(wallet.getCurrency())
                .balance(wallet.getBalance())
                .build();
    }
}
