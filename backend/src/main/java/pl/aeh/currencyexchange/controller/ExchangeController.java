package pl.aeh.currencyexchange.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.aeh.currencyexchange.dto.ExchangeRequestDto;
import pl.aeh.currencyexchange.dto.TransactionDto; 
import pl.aeh.currencyexchange.model.Transaction;
import pl.aeh.currencyexchange.service.WalletService;

@RestController
@RequestMapping("/api/exchange") 
@RequiredArgsConstructor
public class ExchangeController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<TransactionDto> exchangeCurrency(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ExchangeRequestDto request
    ) {
        Transaction transaction = walletService.exchangeCurrency(userDetails.getUsername(), request);

        TransactionDto response = TransactionDto.builder()
                .id(transaction.getId())
                .fromCurrency(transaction.getFromCurrency())
                .toCurrency(transaction.getToCurrency())
                .fromAmount(transaction.getFromAmount())
                .toAmount(transaction.getToAmount())
                .exchangeRate(transaction.getExchangeRate())
                .status(transaction.getStatus().name())
                .createdAt(transaction.getCreatedAt())
                .build();

        return ResponseEntity.ok(response);
    }
}