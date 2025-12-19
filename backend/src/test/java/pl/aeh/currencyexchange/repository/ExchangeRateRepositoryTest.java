package pl.aeh.currencyexchange.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import pl.aeh.currencyexchange.model.ExchangeRate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Exchange Rate Repository Tests")
class ExchangeRateRepositoryTest {

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @BeforeEach
    void setUp() {
        exchangeRateRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save and retrieve exchange rate")
    void shouldSaveAndRetrieveExchangeRate() {
        // Given
        ExchangeRate rate = ExchangeRate.builder()
                .currency("USD")
                .rateDate(LocalDate.now())
                .bid(new BigDecimal("3.95"))
                .ask(new BigDecimal("4.05"))
                .currencyName("US Dollar")
                .build();

        // When
        ExchangeRate saved = exchangeRateRepository.save(rate);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCurrency()).isEqualTo("USD");
        assertThat(saved.getMidRate()).isEqualByComparingTo(new BigDecimal("4.00"));
    }

    @Test
    @DisplayName("Should find latest rate by currency")
    void shouldFindLatestRateByCurrency() {
        // Given
        exchangeRateRepository.save(ExchangeRate.builder()
                .currency("USD").rateDate(LocalDate.now().minusDays(2))
                .bid(new BigDecimal("3.90")).ask(new BigDecimal("4.00")).build());
        
        exchangeRateRepository.save(ExchangeRate.builder()
                .currency("USD").rateDate(LocalDate.now())
                .bid(new BigDecimal("3.95")).ask(new BigDecimal("4.05")).build());

        // When
        Optional<ExchangeRate> latest = exchangeRateRepository.findLatestByCurrency("USD");

        // Then
        assertThat(latest).isPresent();
        assertThat(latest.get().getRateDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Should find all distinct currencies")
    void shouldFindAllDistinctCurrencies() {
        // Given
        exchangeRateRepository.save(ExchangeRate.builder().currency("USD").rateDate(LocalDate.now()).bid(BigDecimal.ONE).ask(BigDecimal.ONE).build());
        exchangeRateRepository.save(ExchangeRate.builder().currency("EUR").rateDate(LocalDate.now()).bid(BigDecimal.ONE).ask(BigDecimal.ONE).build());
        exchangeRateRepository.save(ExchangeRate.builder().currency("GBP").rateDate(LocalDate.now()).bid(BigDecimal.ONE).ask(BigDecimal.ONE).build());

        // When
        List<String> currencies = exchangeRateRepository.findAllDistinctCurrencies();

        // Then
        assertThat(currencies).containsExactly("EUR", "GBP", "USD");
    }
}
