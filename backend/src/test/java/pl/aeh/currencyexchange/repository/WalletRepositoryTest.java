package pl.aeh.currencyexchange.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import pl.aeh.currencyexchange.model.User;
import pl.aeh.currencyexchange.model.UserRole;
import pl.aeh.currencyexchange.model.Wallet;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Wallet Repository Tests")
class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        walletRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .email("wallet-test@example.com")
                .password("password123")
                .role(UserRole.USER)
                .enabled(true)
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    @DisplayName("Should save and retrieve wallet")
    void shouldSaveAndRetrieveWallet() {
        // Given
        Wallet wallet = Wallet.builder()
                .user(testUser)
                .currency("PLN")
                .balance(new BigDecimal("1000.00"))
                .build();

        // When
        Wallet savedWallet = walletRepository.save(wallet);

        // Then
        assertThat(savedWallet).isNotNull();
        assertThat(savedWallet.getId()).isNotNull();
        assertThat(savedWallet.getCurrency()).isEqualTo("PLN");
        assertThat(savedWallet.getBalance()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(savedWallet.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should find wallet by user ID and currency")
    void shouldFindWalletByUserIdAndCurrency() {
        // Given
        Wallet wallet = Wallet.builder()
                .user(testUser)
                .currency("USD")
                .balance(new BigDecimal("500.00"))
                .build();
        walletRepository.save(wallet);

        // When
        Optional<Wallet> found = walletRepository.findByUserIdAndCurrency(testUser.getId(), "USD");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCurrency()).isEqualTo("USD");
        assertThat(found.get().getBalance()).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    @Test
    @DisplayName("Should find all wallets for user")
    void shouldFindAllWalletsForUser() {
        // Given
        Wallet plnWallet = Wallet.builder()
                .user(testUser)
                .currency("PLN")
                .balance(new BigDecimal("1000.00"))
                .build();

        Wallet usdWallet = Wallet.builder()
                .user(testUser)
                .currency("USD")
                .balance(new BigDecimal("250.00"))
                .build();

        Wallet eurWallet = Wallet.builder()
                .user(testUser)
                .currency("EUR")
                .balance(new BigDecimal("100.00"))
                .build();

        walletRepository.saveAll(List.of(plnWallet, usdWallet, eurWallet));

        // When
        List<Wallet> wallets = walletRepository.findAllByUserId(testUser.getId());

        // Then
        assertThat(wallets).hasSize(3);
        assertThat(wallets)
                .extracting(Wallet::getCurrency)
                .containsExactlyInAnyOrder("PLN", "USD", "EUR");
    }

    @Test
    @DisplayName("Should find wallets ordered by currency")
    void shouldFindWalletsOrderedByCurrency() {
        // Given
        walletRepository.save(Wallet.builder().user(testUser).currency("USD").balance(BigDecimal.TEN).build());
        walletRepository.save(Wallet.builder().user(testUser).currency("EUR").balance(BigDecimal.TEN).build());
        walletRepository.save(Wallet.builder().user(testUser).currency("GBP").balance(BigDecimal.TEN).build());

        // When
        List<Wallet> wallets = walletRepository.findAllByUserIdOrderByCurrencyAsc(testUser.getId());

        // Then
        assertThat(wallets)
                .extracting(Wallet::getCurrency)
                .containsExactly("EUR", "GBP", "USD");
    }

    @Test
    @DisplayName("Should enforce unique constraint on user_id and currency")
    void shouldEnforceUniqueConstraintOnUserIdAndCurrency() {
        // Given
        Wallet wallet1 = Wallet.builder()
                .user(testUser)
                .currency("PLN")
                .balance(new BigDecimal("1000.00"))
                .build();
        walletRepository.save(wallet1);

        Wallet wallet2 = Wallet.builder()
                .user(testUser)
                .currency("PLN")
                .balance(new BigDecimal("2000.00"))
                .build();

        // When/Then
        assertThatThrownBy(() -> {
            walletRepository.saveAndFlush(wallet2);
        }).isNotNull();
    }

    @Test
    @DisplayName("Should check if wallet exists")
    void shouldCheckIfWalletExists() {
        // Given
        Wallet wallet = Wallet.builder()
                .user(testUser)
                .currency("EUR")
                .balance(BigDecimal.ZERO)
                .build();
        walletRepository.save(wallet);

        // When
        boolean exists = walletRepository.existsByUserIdAndCurrency(testUser.getId(), "EUR");
        boolean notExists = walletRepository.existsByUserIdAndCurrency(testUser.getId(), "GBP");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should get balance for wallet")
    void shouldGetBalanceForWallet() {
        // Given
        Wallet wallet = Wallet.builder()
                .user(testUser)
                .currency("PLN")
                .balance(new BigDecimal("1234.56"))
                .build();
        walletRepository.save(wallet);

        // When
        Optional<BigDecimal> balance = walletRepository.getBalanceByUserIdAndCurrency(testUser.getId(), "PLN");

        // Then
        assertThat(balance).isPresent();
        assertThat(balance.get()).isEqualByComparingTo(new BigDecimal("1234.56"));
    }

    @Test
    @DisplayName("Should find only active wallets (balance > 0)")
    void shouldFindOnlyActiveWallets() {
        // Given
        walletRepository.save(Wallet.builder().user(testUser).currency("PLN").balance(new BigDecimal("100.00")).build());
        walletRepository.save(Wallet.builder().user(testUser).currency("USD").balance(BigDecimal.ZERO).build());
        walletRepository.save(Wallet.builder().user(testUser).currency("EUR").balance(new BigDecimal("50.00")).build());

        // When
        List<Wallet> activeWallets = walletRepository.findAllActiveWalletsByUserId(testUser.getId());

        // Then
        assertThat(activeWallets).hasSize(2);
        assertThat(activeWallets)
                .extracting(Wallet::getCurrency)
                .containsExactlyInAnyOrder("PLN", "EUR");
    }

    @Test
    @DisplayName("Should count wallets for user")
    void shouldCountWalletsForUser() {
        // Given
        walletRepository.save(Wallet.builder().user(testUser).currency("PLN").balance(BigDecimal.TEN).build());
        walletRepository.save(Wallet.builder().user(testUser).currency("USD").balance(BigDecimal.TEN).build());

        // When
        long count = walletRepository.countByUserId(testUser.getId());

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should update wallet balance")
    void shouldUpdateWalletBalance() {
        // Given
        Wallet wallet = Wallet.builder()
                .user(testUser)
                .currency("PLN")
                .balance(new BigDecimal("1000.00"))
                .build();
        wallet = walletRepository.save(wallet);

        // When
        wallet.setBalance(new BigDecimal("1500.00"));
        Wallet updated = walletRepository.save(wallet);

        // Then
        assertThat(updated.getBalance()).isEqualByComparingTo(new BigDecimal("1500.00"));
        assertThat(updated.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should enforce positive balance constraint")
    void shouldEnforcePositiveBalanceConstraint() {
        // Given
        Wallet wallet = Wallet.builder()
                .user(testUser)
                .currency("PLN")
                .balance(new BigDecimal("-100.00"))
                .build();

        // When/Then
        assertThatThrownBy(() -> {
            walletRepository.saveAndFlush(wallet);
        }).isNotNull();
    }

    @Test
    @DisplayName("Should test wallet helper methods")
    void shouldTestWalletHelperMethods() {
        // Given
        Wallet wallet = Wallet.builder()
                .user(testUser)
                .currency("PLN")
                .balance(new BigDecimal("1000.00"))
                .build();

        // When - deposit
        wallet.deposit(new BigDecimal("500.00"));

        // Then
        assertThat(wallet.getBalance()).isEqualByComparingTo(new BigDecimal("1500.00"));

        // When - withdraw
        wallet.withdraw(new BigDecimal("300.00"));

        // Then
        assertThat(wallet.getBalance()).isEqualByComparingTo(new BigDecimal("1200.00"));
    }

    @Test
    @DisplayName("Should throw exception when withdrawing more than balance")
    void shouldThrowExceptionWhenWithdrawingMoreThanBalance() {
        // Given
        Wallet wallet = Wallet.builder()
                .user(testUser)
                .currency("PLN")
                .balance(new BigDecimal("100.00"))
                .build();

        // When/Then
        assertThatThrownBy(() -> {
            wallet.withdraw(new BigDecimal("200.00"));
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Insufficient funds");
    }
}
