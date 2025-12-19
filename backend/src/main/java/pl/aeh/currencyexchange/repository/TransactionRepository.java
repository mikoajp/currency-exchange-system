package pl.aeh.currencyexchange.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.aeh.currencyexchange.model.Transaction;
import pl.aeh.currencyexchange.model.TransactionStatus;
import pl.aeh.currencyexchange.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Find all transactions for a user
     *
     * @param userId user's ID
     * @return List of transactions
     */
    List<Transaction> findAllByUserId(Long userId);

    /**
     * Find all transactions for a user ordered by creation date descending
     *
     * @param userId user's ID
     * @return List of transactions ordered by date
     */
    List<Transaction> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find all transactions for a user with pagination
     *
     * @param userId   user's ID
     * @param pageable pagination information
     * @return Page of transactions
     */
    Page<Transaction> findAllByUserId(Long userId, Pageable pageable);

    /**
     * Find transactions by user ID and type
     *
     * @param userId user's ID
     * @param type   transaction type
     * @return List of transactions
     */
    List<Transaction> findAllByUserIdAndType(Long userId, TransactionType type);

    /**
     * Find transactions by user ID and status
     *
     * @param userId user's ID
     * @param status transaction status
     * @return List of transactions
     */
    List<Transaction> findAllByUserIdAndStatus(Long userId, TransactionStatus status);

    /**
     * Find transactions by user ID within date range
     *
     * @param userId user's ID
     * @param from   start date
     * @param to     end date
     * @return List of transactions
     */
    List<Transaction> findAllByUserIdAndCreatedAtBetween(Long userId, LocalDateTime from, LocalDateTime to);

    /**
     * Find transactions by user ID, type and date range
     *
     * @param userId user's ID
     * @param type   transaction type
     * @param from   start date
     * @param to     end date
     * @return List of transactions
     */
    List<Transaction> findAllByUserIdAndTypeAndCreatedAtBetween(
            Long userId, TransactionType type, LocalDateTime from, LocalDateTime to
    );

    /**
     * Count transactions by user ID
     *
     * @param userId user's ID
     * @return count of transactions
     */
    long countByUserId(Long userId);

    /**
     * Count transactions by user ID and type
     *
     * @param userId user's ID
     * @param type   transaction type
     * @return count of transactions
     */
    long countByUserIdAndType(Long userId, TransactionType type);

    /**
     * Get total amount exchanged by user for specific currency
     *
     * @param userId   user's ID
     * @param currency currency code
     * @return total amount
     */
    @Query("SELECT COALESCE(SUM(t.fromAmount), 0) FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.fromCurrency = :currency AND t.status = 'COMPLETED'")
    BigDecimal getTotalAmountExchangedFromCurrency(@Param("userId") Long userId, @Param("currency") String currency);

    /**
     * Get total amount received by user for specific currency
     *
     * @param userId   user's ID
     * @param currency currency code
     * @return total amount
     */
    @Query("SELECT COALESCE(SUM(t.toAmount), 0) FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.toCurrency = :currency AND t.status = 'COMPLETED'")
    BigDecimal getTotalAmountExchangedToCurrency(@Param("userId") Long userId, @Param("currency") String currency);

    /**
     * Find recent transactions for user (last N transactions)
     *
     * @param userId   user's ID
     * @param pageable pagination with limit
     * @return Page of recent transactions
     */
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId ORDER BY t.createdAt DESC")
    Page<Transaction> findRecentTransactions(@Param("userId") Long userId, Pageable pageable);

    /**
     * Get most traded currency for user
     *
     * @param userId user's ID
     * @return most traded currency code
     */
    @Query("SELECT t.fromCurrency FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.status = 'COMPLETED' " +
            "GROUP BY t.fromCurrency " +
            "ORDER BY COUNT(t) DESC " +
            "LIMIT 1")
    String findMostTradedCurrency(@Param("userId") Long userId);

    /**
     * Find transactions involving specific currency
     *
     * @param userId   user's ID
     * @param currency currency code
     * @return List of transactions
     */
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
            "AND (t.fromCurrency = :currency OR t.toCurrency = :currency) " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> findTransactionsByCurrency(@Param("userId") Long userId, @Param("currency") String currency);

    /**
     * Check if user has any completed transactions
     *
     * @param userId user's ID
     * @return true if user has completed transactions
     */
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.status = 'COMPLETED'")
    boolean hasCompletedTransactions(@Param("userId") Long userId);
}
