package pl.aeh.currencyexchange.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.aeh.currencyexchange.dto.ExchangeRequestDto;
import pl.aeh.currencyexchange.dto.TransactionDto;
import pl.aeh.currencyexchange.service.AuthService;
import pl.aeh.currencyexchange.service.ExchangeService;
import pl.aeh.currencyexchange.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final ExchangeService exchangeService;
    private final TransactionService transactionService;
    private final AuthService authService;

    @PostMapping("/exchange")
    public ResponseEntity<TransactionDto> exchangeCurrency(@Valid @RequestBody ExchangeRequestDto request) {
        Long userId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(exchangeService.performExchange(userId, request));
    }

    @GetMapping("/history")
    public ResponseEntity<Page<TransactionDto>> getHistory(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long userId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(transactionService.getUserTransactionHistory(userId, pageable));
    }

    @GetMapping("/summary")
    public ResponseEntity<pl.aeh.currencyexchange.dto.TransactionSummaryDto> getSummary() {
        Long userId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(transactionService.getUserSummary(userId));
    }
}
