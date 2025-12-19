package pl.aeh.currencyexchange.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.aeh.currencyexchange.model.ExchangeRate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    /**
     * Find exchange rate by currency code
     * Returns the most recent rate for the currency
     *
     * @param currency currency code (e.g., "USD", "EUR")
     * @return Optional containing exchange rate if found
     */
    @Query("SELECT e FROM ExchangeRate e WHERE e.currency = :currency ORDER BY e.rateDate DESC LIMIT 1")
    Optional<ExchangeRate> findLatestByCurrency(@Param("currency") String currency);

    /**
     * Find exchange rate by currency and specific date
     *
     * @param currency currency code
     * @param date     rate date
     * @return Optional containing exchange rate if found
     */
    Optional<ExchangeRate> findByCurrencyAndRateDate(String currency, LocalDate date);

    /**
     * Find all latest exchange rates (one per currency)
     *
     * @return List of latest exchange rates
     */
    @Query("SELECT e FROM ExchangeRate e WHERE e.rateDate = " +
            "(SELECT MAX(e2.rateDate) FROM ExchangeRate e2 WHERE e2.currency = e.currency)")
    List<ExchangeRate> findAllLatest();

    /**
     * Find all exchange rates for a specific date
     *
     * @param date rate date
     * @return List of exchange rates
     */
    List<ExchangeRate> findAllByRateDate(LocalDate date);

    /**
     * Find all exchange rates for a currency ordered by date descending
     *
     * @param currency currency code
     * @return List of exchange rates
     */
    List<ExchangeRate> findAllByCurrencyOrderByRateDateDesc(String currency);

    /**
     * Find exchange rates for a currency within date range
     *
     * @param currency currency code
     * @param fromDate start date
     * @param toDate   end date
     * @return List of exchange rates
     */
    List<ExchangeRate> findAllByCurrencyAndRateDateBetween(String currency, LocalDate fromDate, LocalDate toDate);

    /**
     * Check if exchange rate exists for currency and date
     *
     * @param currency currency code
     * @param date     rate date
     * @return true if rate exists, false otherwise
     */
    boolean existsByCurrencyAndRateDate(String currency, LocalDate date);

    /**
     * Get all unique currency codes
     *
     * @return List of currency codes
     */
    @Query("SELECT DISTINCT e.currency FROM ExchangeRate e ORDER BY e.currency")
    List<String> findAllDistinctCurrencies();

    /**
     * Find the latest date for which rates are available
     *
     * @return Optional containing the latest date
     */
    @Query("SELECT MAX(e.rateDate) FROM ExchangeRate e")
    Optional<LocalDate> findLatestRateDate();

    /**
     * Find latest rate date for specific currency
     *
     * @param currency currency code
     * @return Optional containing the latest date for the currency
     */
    @Query("SELECT MAX(e.rateDate) FROM ExchangeRate e WHERE e.currency = :currency")
    Optional<LocalDate> findLatestRateDateByCurrency(@Param("currency") String currency);

    /**
     * Count exchange rates for specific currency
     *
     * @param currency currency code
     * @return count of rates
     */
    long countByCurrency(String currency);

    /**
     * Delete old exchange rates (older than specified date)
     *
     * @param date cutoff date
     */
    void deleteByRateDateBefore(LocalDate date);

    /**
     * Find exchange rates updated after specific date (for cache refresh)
     *
     * @param date date to compare
     * @return List of recently updated rates
     */
    @Query("SELECT e FROM ExchangeRate e WHERE e.rateDate >= :date ORDER BY e.rateDate DESC")
    List<ExchangeRate> findRecentRates(@Param("date") LocalDate date);
}
