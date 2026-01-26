package pl.aeh.currencyexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletDto {
    private Long id;
    
    // ZMIANA: currency -> currencyCode (dla spójności z Wallet.java i WalletRepository)
    private String currency; 
    
    private BigDecimal balance;
}