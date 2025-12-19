package pl.aeh.currencyexchange.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import pl.aeh.currencyexchange.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Transaction Repository Tests")
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .email("transaction-test@example.com")
                .password("password123")
                .role(UserRole.USER)
                .enabled(true)
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    @DisplayName("Should save and retrieve transaction")
    void shouldSaveAndRetrieveTransaction() {
        // Given
        Transaction transaction = Transaction.builder()
                .user(testUser)
                .type(TransactionType.DEPOSIT)
                .fromCurrency("PLN")
                .fromAmount(new BigDecimal("1000.00"))
                .status(TransactionStatus.COMPLETED)
                .build();

        // When
        Transaction saved = transactionRepository.save(transaction);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getType()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should find all transactions for user ordered by date")
    void shouldFindAllTransactionsOrderedByDate() {
        // Given
        transactionRepository.save(Transaction.builder()
                .user(testUser).type(TransactionType.BUY)
                .status(TransactionStatus.COMPLETED).build());
        
        transactionRepository.save(Transaction.builder()
                .user(testUser).type(TransactionType.SELL)
                .status(TransactionStatus.COMPLETED).build());

        // When
        List<Transaction> transactions = transactionRepository.findAllByUserIdOrderByCreatedAtDesc(testUser.getId());

        // Then
        assertThat(transactions).hasSize(2);
    }

    @Test
    @DisplayName("Should find transactions with pagination")
    void shouldFindTransactionsWithPagination() {
        // Given
        for (int i = 0; i < 15; i++) {
            transactionRepository.save(Transaction.builder()
                    .user(testUser)
                    .type(TransactionType.BUY)
                    .status(TransactionStatus.COMPLETED)
                    .build());
        }

        // When
        Page<Transaction> page = transactionRepository.findAllByUserId(testUser.getId(), PageRequest.of(0, 10));

        // Then
        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getTotalElements()).isEqualTo(15);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should count transactions by user")
    void shouldCountTransactionsByUser() {
        // Given
        transactionRepository.save(Transaction.builder().user(testUser).type(TransactionType.BUY).status(TransactionStatus.COMPLETED).build());
        transactionRepository.save(Transaction.builder().user(testUser).type(TransactionType.SELL).status(TransactionStatus.COMPLETED).build());

        // When
        long count = transactionRepository.countByUserId(testUser.getId());

        // Then
        assertThat(count).isEqualTo(2);
    }
}
