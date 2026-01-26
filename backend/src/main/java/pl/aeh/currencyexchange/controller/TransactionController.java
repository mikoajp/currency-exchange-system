package pl.aeh.currencyexchange.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.aeh.currencyexchange.dto.TransactionDto;
import pl.aeh.currencyexchange.service.WalletService;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final WalletService walletService;

    @GetMapping
    public ResponseEntity<List<TransactionDto>> getHistory(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(walletService.getTransactionHistory(userDetails.getUsername()));
    }
}