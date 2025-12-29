package pl.aeh.currencyexchange.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.aeh.currencyexchange.dto.ExchangeRateDto;
import pl.aeh.currencyexchange.security.JwtAuthenticationFilter;
import pl.aeh.currencyexchange.security.JwtUtil;
import pl.aeh.currencyexchange.service.ExchangeRateService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExchangeRateController.class)
@AutoConfigureMockMvc(addFilters = false)
class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService exchangeRateService;
    
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @MockBean
    private JwtUtil jwtUtil;

    private ExchangeRateDto exchangeRateDto;

    @BeforeEach
    void setUp() {
        exchangeRateDto = ExchangeRateDto.builder()
                .code("USD")
                .currency("dolar amerykański")
                .bid(new BigDecimal("4.0"))
                .ask(new BigDecimal("4.2"))
                .rateDate(LocalDate.now())
                .build();
    }

    @Test
    void shouldGetCurrentRates() throws Exception {
        given(exchangeRateService.fetchCurrentRates()).willReturn(Collections.singletonList(exchangeRateDto));

        mockMvc.perform(get("/api/rates/current")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("USD"));
    }

    @Test
    void shouldGetRateByCode() throws Exception {
        given(exchangeRateService.getCurrentRate("USD")).willReturn(exchangeRateDto);

        mockMvc.perform(get("/api/rates/currency/USD")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("USD"));
    }

    @Test
    void shouldGetRateHistory() throws Exception {
        given(exchangeRateService.getRateHistory(anyString(), any(), any()))
                .willReturn(Collections.singletonList(exchangeRateDto));

        mockMvc.perform(get("/api/rates/history/USD")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("USD"));
    }
}
