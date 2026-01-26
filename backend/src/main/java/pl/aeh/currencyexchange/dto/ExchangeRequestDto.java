package pl.aeh.currencyexchange.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ExchangeRequestDto {
    private String fromCurrency; // np. "PLN"
    private String toCurrency;   // np. "USD"
    private BigDecimal amount;   // Ilość waluty źródłowej, którą chcesz wydać
}