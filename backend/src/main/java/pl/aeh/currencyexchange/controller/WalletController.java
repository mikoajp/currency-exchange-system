package pl.aeh.currencyexchange.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.aeh.currencyexchange.dto.ErrorResponseDto;
import pl.aeh.currencyexchange.dto.WalletDto;
import pl.aeh.currencyexchange.service.WalletService;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Wallets", description = "Endpoints for managing user wallets")
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/me")
    @Operation(
            summary = "Get user wallets",
            description = "Returns a list of wallets belonging to the currently authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Wallets retrieved successfully",
                    content = @Content(schema = @Schema(implementation = WalletDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    public ResponseEntity<List<WalletDto>> getUserWallets() {
        log.debug("GET /api/wallets/me - Getting user wallets");
        List<WalletDto> wallets = walletService.getUserWallets();
        return ResponseEntity.ok(wallets);
    }
}
