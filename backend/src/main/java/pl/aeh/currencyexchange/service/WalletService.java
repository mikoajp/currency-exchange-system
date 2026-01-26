package pl.aeh.currencyexchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.aeh.currencyexchange.dto.ExchangeRateDto;
import pl.aeh.currencyexchange.dto.ExchangeRequestDto;
import pl.aeh.currencyexchange.dto.TransactionDto; 
import pl.aeh.currencyexchange.dto.WalletDto;
import pl.aeh.currencyexchange.exception.InvalidCredentialsException;
import pl.aeh.currencyexchange.exception.ResourceNotFoundException;
import pl.aeh.currencyexchange.model.*; 
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

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final ExchangeRateService exchangeRateService;

    @Transactional(readOnly = true)
    public List<WalletDto> getUserWallets() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            throw new InvalidCredentialsException("No authenticated user found");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return walletRepository.findAllByUserIdOrderByCurrencyAsc(user.getId()).stream()
                .map(this::mapToWalletDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Wallet topUpWallet(String email, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Wallet wallet = user.getWallets().stream()
                .filter(w -> "PLN".equals(w.getCurrency()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("PLN Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(amount));
        Wallet savedWallet = walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .user(user)
                .type(TransactionType.DEPOSIT)
                .fromCurrency("PLN")
                .toCurrency("PLN")
                .fromAmount(amount)
                .toAmount(amount)
                .exchangeRate(BigDecimal.ONE)
                .status(TransactionStatus.COMPLETED)
                .description("PayPal Top-up (Sandbox Simulation)")
                .build();

        transactionRepository.save(transaction);

        return savedWallet;
    }

    @Transactional
    public Transaction exchangeCurrency(String email, ExchangeRequestDto request) {
        String fromCurr = request.getFromCurrency().toUpperCase();
        String toCurr = request.getToCurrency().toUpperCase();
        BigDecimal amount = request.getAmount();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (fromCurr.equals(toCurr)) {
            throw new IllegalArgumentException("Source and target currency cannot be the same");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Wallet fromWallet = user.getWallets().stream()
                .filter(w -> w.getCurrency().equals(fromCurr))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found: " + fromCurr));

        if (fromWallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds in " + fromCurr + " wallet");
        }

        BigDecimal rate;
        BigDecimal targetAmount;

        if ("PLN".equals(fromCurr)) {
            ExchangeRateDto rateDto = exchangeRateService.getCurrentRate(toCurr);
            rate = rateDto.getAsk(); 
            targetAmount = amount.divide(rate, 4, java.math.RoundingMode.HALF_UP);
        }
        else if ("PLN".equals(toCurr)) {
            ExchangeRateDto rateDto = exchangeRateService.getCurrentRate(fromCurr);
            rate = rateDto.getBid(); 
            targetAmount = amount.multiply(rate).setScale(4, java.math.RoundingMode.HALF_UP);
        }
        else {
            ExchangeRateDto fromRateDto = exchangeRateService.getCurrentRate(fromCurr);
            BigDecimal plnAmount = amount.multiply(fromRateDto.getBid());
            
            ExchangeRateDto toRateDto = exchangeRateService.getCurrentRate(toCurr);
            BigDecimal finalRate = toRateDto.getAsk();
            
            targetAmount = plnAmount.divide(finalRate, 4, java.math.RoundingMode.HALF_UP);
            
            rate = targetAmount.divide(amount, 6, java.math.RoundingMode.HALF_UP);
        }

        Wallet toWallet = user.getWallets().stream()
                .filter(w -> w.getCurrency().equals(toCurr))
                .findFirst()
                .orElseGet(() -> {
                    Wallet newWallet = new Wallet();
                    newWallet.setUser(user);
                    newWallet.setCurrency(toCurr);
                    newWallet.setBalance(BigDecimal.ZERO);
                    return walletRepository.save(newWallet);
                });

        fromWallet.setBalance(fromWallet.getBalance().subtract(amount));
        toWallet.setBalance(toWallet.getBalance().add(targetAmount));

        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        Transaction transaction = Transaction.builder()
                .user(user)
                .type(TransactionType.BUY)
                .fromCurrency(fromCurr)
                .toCurrency(toCurr)
                .fromAmount(amount)
                .toAmount(targetAmount)
                .exchangeRate(rate)
                .status(TransactionStatus.COMPLETED)
                .description("Exchange " + fromCurr + " to " + toCurr)
                .build();

        return transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> getTransactionHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return transactionRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(t -> TransactionDto.builder()
                        .id(t.getId())
                        .fromCurrency(t.getFromCurrency())
                        .toCurrency(t.getToCurrency())
                        .fromAmount(t.getFromAmount())
                        .toAmount(t.getToAmount())
                        .exchangeRate(t.getExchangeRate())
                        .status(t.getStatus().name())
                        .createdAt(t.getCreatedAt())
                        .type(t.getType().name()) 
                        .build())
                .collect(Collectors.toList());
    }

    private WalletDto mapToWalletDto(Wallet wallet) {
        return WalletDto.builder()
                .id(wallet.getId())
                .currency(wallet.getCurrency())
                .balance(wallet.getBalance())
                .build();
    }
}