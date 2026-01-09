package pl.aeh.currencyexchange.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.aeh.currencyexchange.dto.DepositDto;
import pl.aeh.currencyexchange.dto.WalletDto;
import pl.aeh.currencyexchange.service.AuthService;
import pl.aeh.currencyexchange.service.WalletService;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<WalletDto>> getUserWallets() {
        Long userId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(walletService.getUserWallets(userId));
    }

    @GetMapping("/{currency}")
    public ResponseEntity<WalletDto> getWallet(@PathVariable String currency) {
        Long userId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(walletService.getOrCreateWallet(userId, currency));
    }

    @PostMapping("/deposit")
    public ResponseEntity<WalletDto> deposit(@Valid @RequestBody DepositDto depositDto) {
        Long userId = authService.getCurrentUser().getId();
        return ResponseEntity.ok(walletService.depositPLN(userId, depositDto.getAmount()));
    }
}
