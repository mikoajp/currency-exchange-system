package pl.aeh.currencyexchange.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TopUpDto {
    private BigDecimal amount;
}