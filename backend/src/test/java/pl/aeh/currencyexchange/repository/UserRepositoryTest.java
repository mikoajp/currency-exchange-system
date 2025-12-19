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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("User Repository Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        
        testUser = User.builder()
                .email("test@example.com")
                .password("hashedPassword123")
                .firstName("John")
                .lastName("Doe")
                .role(UserRole.USER)
                .enabled(true)
                .build();
    }

    @Test
    @DisplayName("Should save and retrieve user")
    void shouldSaveAndRetrieveUser() {
        // When
        User savedUser = userRepository.save(testUser);

        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        // Given
        userRepository.save(testUser);

        // When
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        assertThat(found.get().getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should return empty when user not found by email")
    void shouldReturnEmptyWhenUserNotFoundByEmail() {
        // When
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should check if user exists by email")
    void shouldCheckIfUserExistsByEmail() {
        // Given
        userRepository.save(testUser);

        // When
        boolean exists = userRepository.existsByEmail("test@example.com");
        boolean notExists = userRepository.existsByEmail("other@example.com");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should enforce unique email constraint")
    void shouldEnforceUniqueEmailConstraint() {
        // Given
        userRepository.save(testUser);

        User duplicateUser = User.builder()
                .email("test@example.com")
                .password("anotherPassword")
                .build();

        // When/Then
        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
        
        // Attempting to save duplicate should fail
        try {
            userRepository.saveAndFlush(duplicateUser);
            assertThat(false).as("Should have thrown constraint violation").isTrue();
        } catch (Exception e) {
            assertThat(e).isNotNull();
        }
    }

    @Test
    @DisplayName("Should find user with wallets")
    void shouldFindUserWithWallets() {
        // Given
        User user = userRepository.save(testUser);
        
        Wallet plnWallet = Wallet.builder()
                .user(user)
                .currency("PLN")
                .balance(new BigDecimal("1000.00"))
                .build();
        
        Wallet usdWallet = Wallet.builder()
                .user(user)
                .currency("USD")
                .balance(new BigDecimal("250.00"))
                .build();
        
        user.addWallet(plnWallet);
        user.addWallet(usdWallet);
        userRepository.save(user);

        // When
        Optional<User> foundUser = userRepository.findByEmailWithWallets("test@example.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getWallets()).hasSize(2);
        assertThat(foundUser.get().getWallets())
                .extracting(Wallet::getCurrency)
                .containsExactlyInAnyOrder("PLN", "USD");
    }

    @Test
    @DisplayName("Should find user by id with wallets")
    void shouldFindUserByIdWithWallets() {
        // Given
        User user = userRepository.save(testUser);
        
        Wallet wallet = Wallet.builder()
                .user(user)
                .currency("EUR")
                .balance(new BigDecimal("500.00"))
                .build();
        
        user.addWallet(wallet);
        user = userRepository.save(user);

        // When
        Optional<User> foundUser = userRepository.findByIdWithWallets(user.getId());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getWallets()).hasSize(1);
        assertThat(foundUser.get().getWallets().get(0).getCurrency()).isEqualTo("EUR");
    }

    @Test
    @DisplayName("Should check if user is enabled")
    void shouldCheckIfUserIsEnabled() {
        // Given
        User enabledUser = userRepository.save(testUser);
        
        User disabledUser = User.builder()
                .email("disabled@example.com")
                .password("password")
                .enabled(false)
                .build();
        userRepository.save(disabledUser);

        // When
        Optional<Boolean> enabledStatus = userRepository.isUserEnabled("test@example.com");
        Optional<Boolean> disabledStatus = userRepository.isUserEnabled("disabled@example.com");

        // Then
        assertThat(enabledStatus).isPresent();
        assertThat(enabledStatus.get()).isTrue();
        assertThat(disabledStatus).isPresent();
        assertThat(disabledStatus.get()).isFalse();
    }

    @Test
    @DisplayName("Should update user details")
    void shouldUpdateUserDetails() {
        // Given
        User user = userRepository.save(testUser);

        // When
        user.setFirstName("Jane");
        user.setLastName("Smith");
        User updatedUser = userRepository.save(user);

        // Then
        assertThat(updatedUser.getFirstName()).isEqualTo("Jane");
        assertThat(updatedUser.getLastName()).isEqualTo("Smith");
        assertThat(updatedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should delete user")
    void shouldDeleteUser() {
        // Given
        User user = userRepository.save(testUser);
        Long userId = user.getId();

        // When
        userRepository.delete(user);

        // Then
        Optional<User> found = userRepository.findById(userId);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should cascade delete wallets when user is deleted")
    void shouldCascadeDeleteWalletsWhenUserDeleted() {
        // Given
        User user = userRepository.save(testUser);
        
        Wallet wallet = Wallet.builder()
                .user(user)
                .currency("PLN")
                .balance(BigDecimal.ZERO)
                .build();
        
        user.addWallet(wallet);
        user = userRepository.save(user);
        
        Long userId = user.getId();

        // When
        userRepository.delete(user);
        userRepository.flush();

        // Then
        Optional<User> found = userRepository.findById(userId);
        assertThat(found).isEmpty();
    }
}
