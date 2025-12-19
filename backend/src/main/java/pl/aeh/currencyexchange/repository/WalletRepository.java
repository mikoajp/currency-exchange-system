package pl.aeh.currencyexchange.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.aeh.currencyexchange.model.Wallet;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    /**
     * Find wallet by user ID and currency
     *
     * @param userId   user's ID
     * @param currency currency code (e.g., "PLN", "USD")
     * @return Optional containing wallet if found
     */
    Optional<Wallet> findByUserIdAndCurrency(Long userId, String currency);

    /**
     * Find wallet by user ID and currency with pessimistic write lock
     * Used for concurrent transaction safety
     *
     * @param userId   user's ID
     * @param currency currency code
     * @return Optional containing locked wallet
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.user.id = :userId AND w.currency = :currency")
    Optional<Wallet> findByUserIdAndCurrencyWithLock(@Param("userId") Long userId, @Param("currency") String currency);

    /**
     * Find all wallets for a user
     *
     * @param userId user's ID
     * @return List of wallets
     */
    List<Wallet> findAllByUserId(Long userId);

    /**
     * Find all wallets for a user ordered by currency
     *
     * @param userId user's ID
     * @return List of wallets ordered by currency
     */
    List<Wallet> findAllByUserIdOrderByCurrencyAsc(Long userId);

    /**
     * Check if wallet exists for user and currency
     *
     * @param userId   user's ID
     * @param currency currency code
     * @return true if wallet exists, false otherwise
     */
    boolean existsByUserIdAndCurrency(Long userId, String currency);

    /**
     * Get balance for specific wallet
     *
     * @param userId   user's ID
     * @param currency currency code
     * @return Optional containing balance
     */
    @Query("SELECT w.balance FROM Wallet w WHERE w.user.id = :userId AND w.currency = :currency")
    Optional<BigDecimal> getBalanceByUserIdAndCurrency(@Param("userId") Long userId, @Param("currency") String currency);

    /**
     * Find all wallets with balance greater than zero
     *
     * @param userId user's ID
     * @return List of wallets with positive balance
     */
    @Query("SELECT w FROM Wallet w WHERE w.user.id = :userId AND w.balance > 0 ORDER BY w.currency")
    List<Wallet> findAllActiveWalletsByUserId(@Param("userId") Long userId);

    /**
     * Get total number of wallets for user
     *
     * @param userId user's ID
     * @return count of wallets
     */
    long countByUserId(Long userId);

    /**
     * Delete wallet if balance is zero
     *
     * @param userId   user's ID
     * @param currency currency code
     */
    void deleteByUserIdAndCurrencyAndBalance(Long userId, String currency, BigDecimal balance);
}
