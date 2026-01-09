package pl.aeh.currencyexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSummaryDto {
    private long totalTransactions;
    private long completedTransactions;
    private String mostTradedCurrency;
    private Map<String, BigDecimal> volumeByCurrency; // Total volume exchanged (bought + sold)
}
