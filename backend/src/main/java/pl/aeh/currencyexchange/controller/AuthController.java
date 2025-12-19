package pl.aeh.currencyexchange.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.aeh.currencyexchange.dto.AuthResponseDto;
import pl.aeh.currencyexchange.dto.ErrorResponseDto;
import pl.aeh.currencyexchange.dto.LoginDto;
import pl.aeh.currencyexchange.dto.RegisterDto;
import pl.aeh.currencyexchange.dto.UserDto;
import pl.aeh.currencyexchange.service.AuthService;

/**
 * REST Controller for authentication operations
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user
     *
     * @param registerDto registration data
     * @return authentication response with JWT token
     */
    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and returns a JWT token. A default PLN wallet is created automatically."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User with this email already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterDto registerDto) {
        log.info("POST /api/auth/register - Registering user with email: {}", registerDto.getEmail());
        AuthResponseDto response = authService.register(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login user
     *
     * @param loginDto login credentials
     * @return authentication response with JWT token
     */
    @PostMapping("/login")
    @Operation(
            summary = "Login user",
            description = "Authenticates user credentials and returns a JWT token"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
        log.info("POST /api/auth/login - Login attempt for email: {}", loginDto.getEmail());
        AuthResponseDto response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Get current authenticated user
     *
     * @return current user information
     */
    @GetMapping("/me")
    @Operation(
            summary = "Get current user",
            description = "Returns information about the currently authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User information retrieved",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    public ResponseEntity<UserDto> getCurrentUser() {
        log.debug("GET /api/auth/me - Getting current user");
        UserDto user = authService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    /**
     * Health check endpoint
     *
     * @return status message
     */
    @GetMapping("/health")
    @Operation(
            summary = "Health check",
            description = "Check if authentication service is running"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Service is healthy"
    )
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Authentication service is running");
    }
}
