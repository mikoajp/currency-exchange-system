package pl.aeh.currencyexchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pl.aeh.currencyexchange.config.SecurityConfig;
import pl.aeh.currencyexchange.dto.AuthResponseDto;
import pl.aeh.currencyexchange.dto.LoginDto;
import pl.aeh.currencyexchange.dto.RegisterDto;
import pl.aeh.currencyexchange.dto.UserDto;
import pl.aeh.currencyexchange.exception.InvalidCredentialsException;
import pl.aeh.currencyexchange.exception.UserAlreadyExistsException;
import pl.aeh.currencyexchange.model.UserRole;
import pl.aeh.currencyexchange.security.CustomUserDetailsService;
import pl.aeh.currencyexchange.security.JwtAuthenticationFilter;
import pl.aeh.currencyexchange.security.JwtUtil;
import pl.aeh.currencyexchange.service.AuthService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@DisplayName("Auth Controller Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;

    // Removed @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private RegisterDto registerDto;
    private LoginDto loginDto;
    private AuthResponseDto authResponseDto;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        registerDto = RegisterDto.builder()
                .email("test@example.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .build();

        loginDto = LoginDto.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        authResponseDto = AuthResponseDto.builder()
                .token("jwt-token")
                .type("Bearer")
                .userId(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .expiresIn(86400000L)
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(UserRole.USER)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() throws Exception {
        // Given
        when(authService.register(any(RegisterDto.class))).thenReturn(authResponseDto);

        // When/Then
        mockMvc.perform(post("/api/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.type").value("Bearer"));
    }

    @Test
    @DisplayName("Should return 409 when registering with existing email")
    void shouldReturn409WhenRegisteringWithExistingEmail() throws Exception {
        // Given
        when(authService.register(any(RegisterDto.class)))
                .thenThrow(new UserAlreadyExistsException("User already exists"));

        // When/Then
        mockMvc.perform(post("/api/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should return 400 for invalid registration data")
    void shouldReturn400ForInvalidRegistrationData() throws Exception {
        // Given
        RegisterDto invalidDto = RegisterDto.builder()
                .email("invalid-email")
                .password("123") // too short
                .build();

        // When/Then
        mockMvc.perform(post("/api/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should login user successfully")
    void shouldLoginUserSuccessfully() throws Exception {
        // Given
        when(authService.login(any(LoginDto.class))).thenReturn(authResponseDto);

        // When/Then
        mockMvc.perform(post("/api/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("Should return 401 for invalid credentials")
    void shouldReturn401ForInvalidCredentials() throws Exception {
        // Given
        when(authService.login(any(LoginDto.class)))
                .thenThrow(new InvalidCredentialsException("Invalid credentials"));

        // When/Then
        mockMvc.perform(post("/api/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should get current user when authenticated")
    void shouldGetCurrentUserWhenAuthenticated() throws Exception {
        // Given
        when(authService.getCurrentUser()).thenReturn(userDto);

        // When/Then
        mockMvc.perform(get("/api/users/me")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Should return health status")
    void shouldReturnHealthStatus() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/users/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Authentication service is running"));
    }
}
