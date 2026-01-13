package pl.aeh.currencyexchange.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.aeh.currencyexchange.dto.ExchangeRateDto;
import pl.aeh.currencyexchange.dto.ErrorResponseDto;
import pl.aeh.currencyexchange.service.ExchangeRateService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rates")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Exchange Rates", description = "Endpoints for retrieving exchange rates")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping("/current")
    @Operation(
            summary = "Get current exchange rates",
            description = "Returns a list of the latest available exchange rates."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = ExchangeRateDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ExchangeRateDto>> getCurrentRates() {
        log.debug("GET /api/rates/current");
        return ResponseEntity.ok(exchangeRateService.fetchCurrentRates());
    }

    @GetMapping("/currency/{code}")
    @Operation(
            summary = "Get latest rate for currency",
            description = "Returns the latest rate for a specific currency code (e.g., USD)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = ExchangeRateDto.class))),
            @ApiResponse(responseCode = "404", description = "Currency not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ExchangeRateDto> getRateByCode(
            @Parameter(description = "Currency code (ISO 4217)", example = "USD")
            @PathVariable String code) {
        log.debug("GET /api/rates/currency/{}", code);
        return ResponseEntity.ok(exchangeRateService.getCurrentRate(code));
    }

    @GetMapping("/history/{code}")
    @Operation(
            summary = "Get rate history for currency",
            description = "Returns historical rates for a currency within a date range."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = ExchangeRateDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid date range")
    })
    public ResponseEntity<List<ExchangeRateDto>> getRateHistory(
            @PathVariable String code,
            @Parameter(description = "Start date (YYYY-MM-DD)") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "End date (YYYY-MM-DD)") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        
        // Default range: last 30 days if not specified
        if (to == null) to = LocalDate.now();
        if (from == null) from = to.minusDays(30);

        log.debug("GET /api/rates/history/{}?from={}&to={}", code, from, to);
        return ResponseEntity.ok(exchangeRateService.getRateHistory(code, from, to));
    }

    @PostMapping("/sync")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Force synchronization with NBP",
            description = "Manually triggers synchronization of rates from NBP API. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Synchronization completed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden (requires ADMIN)")
    })
    public ResponseEntity<String> syncRates() {
        log.info("POST /api/rates/sync - Manual sync triggered");
        exchangeRateService.syncRatesFromNbp();
        return ResponseEntity.ok("Exchange rates synchronized successfully");
    }
}
