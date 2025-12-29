package pl.aeh.currencyexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateDto {
    private Long id;
    private String currency;
    private String code;
    private BigDecimal bid;
    private BigDecimal ask;
    private BigDecimal midRate;
    private LocalDate rateDate;
    private LocalDateTime createdAt;
}
