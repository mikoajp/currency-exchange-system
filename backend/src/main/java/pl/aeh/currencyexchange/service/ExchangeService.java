package pl.aeh.currencyexchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.aeh.currencyexchange.dto.ExchangeRequestDto;
import pl.aeh.currencyexchange.dto.ExchangeRateDto;
import pl.aeh.currencyexchange.dto.TransactionDto;
import pl.aeh.currencyexchange.exception.InsufficientFundsException;
import pl.aeh.currencyexchange.exception.ResourceNotFoundException;
import pl.aeh.currencyexchange.exception.ValidationException;
import pl.aeh.currencyexchange.model.*;
import pl.aeh.currencyexchange.repository.TransactionRepository;
import pl.aeh.currencyexchange.repository.WalletRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final ExchangeRateService exchangeRateService;

    @Transactional
    public TransactionDto performExchange(Long userId, ExchangeRequestDto request) {
        String fromCurrency = request.getFromCurrency();
        String toCurrency = request.getToCurrency();
        BigDecimal amount = request.getAmount();

        if (fromCurrency.equals(toCurrency)) {
            throw new ValidationException("Source and target currency must be different");
        }

        log.info("User {} attempting to exchange {} {} to {}", userId, amount, fromCurrency, toCurrency);

        // Lock source wallet
        Wallet sourceWallet = walletRepository.findByUserIdAndCurrencyWithLock(userId, fromCurrency)
                .orElseThrow(() -> new ResourceNotFoundException("Source wallet not found for currency: " + fromCurrency));

        // Check balance
        if (sourceWallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds in " + fromCurrency + " wallet");
        }

        // Get or create target wallet (and lock it if possible, here we rely on basic save/get)
        Wallet targetWallet = walletRepository.findByUserIdAndCurrency(userId, toCurrency)
                .orElseGet(() -> {
                     return Wallet.builder()
                             .user(sourceWallet.getUser())
                             .currency(toCurrency)
                             .balance(BigDecimal.ZERO)
                             .build();
                });
        
        if (targetWallet.getId() != null) {
            // If it existed, lock it
             targetWallet = walletRepository.findByUserIdAndCurrencyWithLock(userId, toCurrency)
                     .orElseThrow(() -> new RuntimeException("Wallet disappeared unexpectedly"));
        } else {
            // If new, save it
            targetWallet = walletRepository.save(targetWallet);
        }

        // Calculate rate and target amount
        BigDecimal targetAmount;
        BigDecimal exchangeRateValue;

        if ("PLN".equals(fromCurrency)) {
            // PLN -> Foreign (Buy Foreign)
            ExchangeRateDto rate = exchangeRateService.getCurrentRate(toCurrency);
            // We use ASK rate (bank sells currency to us)
            exchangeRateValue = rate.getAsk();
            // amount PLN / ask = Foreign
            targetAmount = amount.divide(exchangeRateValue, 2, RoundingMode.HALF_DOWN);
            
        } else if ("PLN".equals(toCurrency)) {
            // Foreign -> PLN (Sell Foreign)
            ExchangeRateDto rate = exchangeRateService.getCurrentRate(fromCurrency);
            // We use BID rate (bank buys currency from us)
            exchangeRateValue = rate.getBid();
            // amount Foreign * bid = PLN
            targetAmount = amount.multiply(exchangeRateValue).setScale(2, RoundingMode.HALF_DOWN);
            
        } else {
            // Foreign -> Foreign (Cross rate)
            // Sell From -> PLN (Bid)
            ExchangeRateDto fromRate = exchangeRateService.getCurrentRate(fromCurrency);
            BigDecimal plnAmount = amount.multiply(fromRate.getBid());
            
            // Buy To -> PLN (Ask)
            ExchangeRateDto toRate = exchangeRateService.getCurrentRate(toCurrency);
            targetAmount = plnAmount.divide(toRate.getAsk(), 2, RoundingMode.HALF_DOWN);
            
            // Effective rate = targetAmount / amount
            exchangeRateValue = targetAmount.divide(amount, 6, RoundingMode.HALF_DOWN);
        }

        // Update balances
        sourceWallet.withdraw(amount);
        targetWallet.deposit(targetAmount);

        walletRepository.save(sourceWallet);
        walletRepository.save(targetWallet);

        // Save transaction
        Transaction transaction = Transaction.builder()
                .user(sourceWallet.getUser())
                .type(determineTransactionType(fromCurrency, toCurrency))
                .fromCurrency(fromCurrency)
                .toCurrency(toCurrency)
                .fromAmount(amount)
                .toAmount(targetAmount)
                .exchangeRate(exchangeRateValue)
                .status(TransactionStatus.COMPLETED)
                .build();

        transaction = transactionRepository.save(transaction);
        log.info("Exchange completed for user {}: {} {} -> {} {}", userId, amount, fromCurrency, targetAmount, toCurrency);

        return mapToDto(transaction);
    }

    private TransactionType determineTransactionType(String from, String to) {
        if ("PLN".equals(from)) return TransactionType.BUY;
        if ("PLN".equals(to)) return TransactionType.SELL;
        return TransactionType.BUY; // Default for Cross-currency
    }

    private TransactionDto mapToDto(Transaction t) {
        return TransactionDto.builder()
                .id(t.getId())
                .type(t.getType())
                .status(t.getStatus())
                .fromCurrency(t.getFromCurrency())
                .toCurrency(t.getToCurrency())
                .fromAmount(t.getFromAmount())
                .toAmount(t.getToAmount())
                .rate(t.getExchangeRate())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
