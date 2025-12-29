package pl.aeh.currencyexchange.dto.nbp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NbpTableDto {
    private String table;
    private String no;
    private LocalDate tradingDate;
    private LocalDate effectiveDate;
    private List<NbpRateDto> rates;
}
