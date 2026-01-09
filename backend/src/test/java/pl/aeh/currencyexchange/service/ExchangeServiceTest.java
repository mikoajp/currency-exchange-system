package pl.aeh.currencyexchange.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.aeh.currencyexchange.dto.ExchangeRateDto;
import pl.aeh.currencyexchange.dto.ExchangeRequestDto;
import pl.aeh.currencyexchange.dto.TransactionDto;
import pl.aeh.currencyexchange.exception.InsufficientFundsException;
import pl.aeh.currencyexchange.model.Transaction;
import pl.aeh.currencyexchange.model.User;
import pl.aeh.currencyexchange.model.Wallet;
import pl.aeh.currencyexchange.repository.TransactionRepository;
import pl.aeh.currencyexchange.repository.WalletRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ExchangeRateService exchangeRateService;

    @InjectMocks
    private ExchangeService exchangeService;

    private User user;
    private Wallet plnWallet;
    private Wallet usdWallet;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("test@example.com").build();
        
        plnWallet = Wallet.builder()
                .id(1L)
                .user(user)
                .currency("PLN")
                .balance(new BigDecimal("1000.00"))
                .build();
                
        usdWallet = Wallet.builder()
                .id(2L)
                .user(user)
                .currency("USD")
                .balance(BigDecimal.ZERO)
                .build();
    }

    @Test
    void performExchange_ShouldSucceed_WhenBalanceIsSufficient() {
        // Arrange
        ExchangeRequestDto request = ExchangeRequestDto.builder()
                .fromCurrency("PLN")
                .toCurrency("USD")
                .amount(new BigDecimal("100.00"))
                .build();

        ExchangeRateDto rateDto = ExchangeRateDto.builder()
                .ask(new BigDecimal("4.00")) // We buy USD at 4.00 PLN
                .build();

        when(walletRepository.findByUserIdAndCurrencyWithLock(1L, "PLN")).thenReturn(Optional.of(plnWallet));
        when(walletRepository.findByUserIdAndCurrency(1L, "USD")).thenReturn(Optional.of(usdWallet));
        when(walletRepository.findByUserIdAndCurrencyWithLock(1L, "USD")).thenReturn(Optional.of(usdWallet));
        when(exchangeRateService.getCurrentRate("USD")).thenReturn(rateDto);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> {
            Transaction t = i.getArgument(0);
            t.setId(100L);
            return t;
        });

        // Act
        TransactionDto result = exchangeService.performExchange(1L, request);

        // Assert
        assertNotNull(result);
        assertEquals("PLN", result.getFromCurrency());
        assertEquals("USD", result.getToCurrency());
        assertEquals(new BigDecimal("100.00"), result.getFromAmount());
        // 100 PLN / 4.00 = 25.00 USD
        assertEquals(new BigDecimal("25.00"), result.getToAmount()); 
        
        assertEquals(new BigDecimal("900.00"), plnWallet.getBalance());
        assertEquals(new BigDecimal("25.00"), usdWallet.getBalance());
        
        verify(walletRepository, times(2)).save(any(Wallet.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void performExchange_ShouldThrowException_WhenBalanceIsInsufficient() {
        // Arrange
        ExchangeRequestDto request = ExchangeRequestDto.builder()
                .fromCurrency("PLN")
                .toCurrency("USD")
                .amount(new BigDecimal("2000.00")) // More than 1000.00
                .build();

        when(walletRepository.findByUserIdAndCurrencyWithLock(1L, "PLN")).thenReturn(Optional.of(plnWallet));

        // Act & Assert
        assertThrows(InsufficientFundsException.class, () -> 
            exchangeService.performExchange(1L, request)
        );
        
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}
