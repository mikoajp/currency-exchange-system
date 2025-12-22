package pl.aeh.currencyexchange.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.aeh.currencyexchange.client.NbpClient;
import pl.aeh.currencyexchange.dto.ExchangeRateDto;
import pl.aeh.currencyexchange.dto.nbp.NbpRateDto;
import pl.aeh.currencyexchange.dto.nbp.NbpTableDto;
import pl.aeh.currencyexchange.model.ExchangeRate;
import pl.aeh.currencyexchange.repository.ExchangeRateRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    @Mock
    private NbpClient nbpClient;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    private NbpTableDto nbpTableDto;
    private ExchangeRate exchangeRate;

    @BeforeEach
    void setUp() {
        NbpRateDto nbpRate = new NbpRateDto();
        nbpRate.setCode("USD");
        nbpRate.setCurrency("dolar amerykański");
        nbpRate.setBid(new BigDecimal("4.0"));
        nbpRate.setAsk(new BigDecimal("4.2"));

        nbpTableDto = new NbpTableDto();
        nbpTableDto.setEffectiveDate(LocalDate.now());
        nbpTableDto.setRates(Collections.singletonList(nbpRate));

        exchangeRate = ExchangeRate.builder()
                .id(1L)
                .currency("USD")
                .currencyName("dolar amerykański")
                .rateDate(LocalDate.now())
                .bid(new BigDecimal("4.0"))
                .ask(new BigDecimal("4.2"))
                .build();
    }

    @Test
    @DisplayName("Should fetch current rates from NBP and save to DB")
    void shouldFetchCurrentRatesFromNbpAndSave() {
        // Given
        when(nbpClient.fetchCurrentRates()).thenReturn(nbpTableDto);
        when(exchangeRateRepository.existsByCurrencyAndRateDate(anyString(), any(LocalDate.class))).thenReturn(false);
        when(exchangeRateRepository.save(any(ExchangeRate.class))).thenReturn(exchangeRate);

        // When
        List<ExchangeRateDto> result = exchangeRateService.fetchCurrentRates();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("USD");
        verify(exchangeRateRepository).save(any(ExchangeRate.class));
    }

    @Test
    @DisplayName("Should use existing rates from DB if present during fetch")
    void shouldUseExistingRatesIfPresent() {
        // Given
        when(nbpClient.fetchCurrentRates()).thenReturn(nbpTableDto);
        when(exchangeRateRepository.existsByCurrencyAndRateDate(anyString(), any(LocalDate.class))).thenReturn(true);
        when(exchangeRateRepository.findByCurrencyAndRateDate(anyString(), any(LocalDate.class)))
                .thenReturn(Optional.of(exchangeRate));

        // When
        List<ExchangeRateDto> result = exchangeRateService.fetchCurrentRates();

        // Then
        assertThat(result).hasSize(1);
        verify(exchangeRateRepository, never()).save(any(ExchangeRate.class));
    }

    @Test
    @DisplayName("Should fallback to DB when NBP fails")
    void shouldFallbackToDbWhenNbpFails() {
        // Given
        when(nbpClient.fetchCurrentRates()).thenThrow(new RuntimeException("NBP down"));
        when(exchangeRateRepository.findAllLatest()).thenReturn(Collections.singletonList(exchangeRate));

        // When
        List<ExchangeRateDto> result = exchangeRateService.fetchCurrentRates();

        // Then
        assertThat(result).hasSize(1);
        verify(exchangeRateRepository).findAllLatest();
    }

    @Test
    @DisplayName("Should fetch rates by date from DB if exists")
    void shouldFetchRatesByDateFromDb() {
        // Given
        LocalDate date = LocalDate.now();
        when(exchangeRateRepository.findAllByRateDate(date)).thenReturn(Collections.singletonList(exchangeRate));

        // When
        List<ExchangeRateDto> result = exchangeRateService.fetchRatesByDate(date);

        // Then
        assertThat(result).hasSize(1);
        verify(nbpClient, never()).fetchRatesByDate(any());
    }

    @Test
    @DisplayName("Should fetch rates by date from NBP if not in DB")
    void shouldFetchRatesByDateFromNbp() {
        // Given
        LocalDate date = LocalDate.now();
        when(exchangeRateRepository.findAllByRateDate(date)).thenReturn(Collections.emptyList());
        when(nbpClient.fetchRatesByDate(date)).thenReturn(nbpTableDto);
        when(exchangeRateRepository.save(any(ExchangeRate.class))).thenReturn(exchangeRate);

        // When
        List<ExchangeRateDto> result = exchangeRateService.fetchRatesByDate(date);

        // Then
        assertThat(result).hasSize(1);
        verify(nbpClient).fetchRatesByDate(date);
        verify(exchangeRateRepository).save(any(ExchangeRate.class));
    }
}
