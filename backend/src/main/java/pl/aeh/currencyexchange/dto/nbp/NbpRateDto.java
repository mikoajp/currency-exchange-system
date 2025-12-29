package pl.aeh.currencyexchange.dto.nbp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NbpRateDto {
    private String currency;
    private String code;
    private BigDecimal bid;
    private BigDecimal ask;
    private BigDecimal mid;
    
    // Fields for currency history response
    private String no;
    private LocalDate effectiveDate;
    private LocalDate tradingDate;
}
