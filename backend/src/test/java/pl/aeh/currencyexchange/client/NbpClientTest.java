package pl.aeh.currencyexchange.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import pl.aeh.currencyexchange.dto.nbp.NbpRateDto;
import pl.aeh.currencyexchange.dto.nbp.NbpTableDto;
import pl.aeh.currencyexchange.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NbpClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private NbpClient nbpClient;

    private NbpTableDto nbpTableDto;

    @BeforeEach
    void setUp() {
        NbpRateDto rate = new NbpRateDto();
        rate.setCurrency("dolar amerykański");
        rate.setCode("USD");
        rate.setBid(new BigDecimal("4.0000"));
        rate.setAsk(new BigDecimal("4.1000"));

        nbpTableDto = new NbpTableDto();
        nbpTableDto.setTable("C");
        nbpTableDto.setNo("001/C/NBP/2024");
        nbpTableDto.setTradingDate(LocalDate.now());
        nbpTableDto.setEffectiveDate(LocalDate.now());
        nbpTableDto.setRates(Collections.singletonList(rate));
    }

    @Test
    @DisplayName("Should fetch current rates successfully")
    void shouldFetchCurrentRatesSuccessfully() {
        // Given
        NbpTableDto[] responseArray = new NbpTableDto[]{nbpTableDto};
        when(restTemplate.getForObject(anyString(), eq(NbpTableDto[].class))).thenReturn(responseArray);

        // When
        NbpTableDto result = nbpClient.fetchCurrentRates();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTable()).isEqualTo("C");
        assertThat(result.getRates()).hasSize(1);
        assertThat(result.getRates().get(0).getCode()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when NBP returns 404")
    void shouldThrowExceptionWhenNbpReturns404() {
        // Given
        when(restTemplate.getForObject(anyString(), eq(NbpTableDto[].class)))
                .thenThrow(org.springframework.web.client.HttpClientErrorException.create(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        // When/Then
        assertThatThrownBy(() -> nbpClient.fetchCurrentRates())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Should throw RuntimeException on network error")
    void shouldThrowExceptionOnNetworkError() {
        // Given
        when(restTemplate.getForObject(anyString(), eq(NbpTableDto[].class)))
                .thenThrow(new ResourceAccessException("Connection refused"));

        // When/Then
        assertThatThrownBy(() -> nbpClient.fetchCurrentRates())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Could not connect to NBP API");
    }
}
