package pl.aeh.currencyexchange.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.aeh.currencyexchange.dto.AuthResponseDto;
import pl.aeh.currencyexchange.dto.LoginDto;
import pl.aeh.currencyexchange.dto.RegisterDto;
import pl.aeh.currencyexchange.exception.InvalidCredentialsException;
import pl.aeh.currencyexchange.exception.UserAlreadyExistsException;
import pl.aeh.currencyexchange.model.User;
import pl.aeh.currencyexchange.model.UserRole;
import pl.aeh.currencyexchange.repository.UserRepository;
import pl.aeh.currencyexchange.security.JwtUtil;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Service Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterDto registerDto;
    private LoginDto loginDto;
    private User user;

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

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .role(UserRole.USER)
                .enabled(true)
                .build();
    }

    @Test
    @DisplayName("Should register new user successfully")
    void shouldRegisterNewUserSuccessfully() {
        // Given
        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("jwt-token");
        when(jwtUtil.getExpirationTime()).thenReturn(86400000L);

        // When
        AuthResponseDto response = authService.register(registerDto);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        
        verify(userRepository).existsByEmail(registerDto.getEmail());
        verify(passwordEncoder).encode(registerDto.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken(any(UserDetails.class));
    }

    @Test
    @DisplayName("Should throw exception when registering with existing email")
    void shouldThrowExceptionWhenRegisteringWithExistingEmail() {
        // Given
        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> authService.register(registerDto))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("already exists");

        verify(userRepository).existsByEmail(registerDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should login user successfully")
    void shouldLoginUserSuccessfully() {
        // Given
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_USER")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("jwt-token");
        when(jwtUtil.getExpirationTime()).thenReturn(86400000L);

        // When
        AuthResponseDto response = authService.login(loginDto);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(loginDto.getEmail());
        verify(jwtUtil).generateToken(any(UserDetails.class));
    }

    @Test
    @DisplayName("Should throw exception when login with invalid credentials")
    void shouldThrowExceptionWhenLoginWithInvalidCredentials() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Bad credentials"));

        // When/Then
        assertThatThrownBy(() -> authService.login(loginDto))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid email or password");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Should throw exception when login with disabled account")
    void shouldThrowExceptionWhenLoginWithDisabledAccount() {
        // Given
        user.setEnabled(false);
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_USER")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));

        // When/Then
        assertThatThrownBy(() -> authService.login(loginDto))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("disabled");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(loginDto.getEmail());
    }

    @Test
    @DisplayName("Should create default PLN wallet on registration")
    void shouldCreateDefaultPlnWalletOnRegistration() {
        // Given
        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerDto.getPassword())).thenReturn("encodedPassword");
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("jwt-token");
        when(jwtUtil.getExpirationTime()).thenReturn(86400000L);
        
        // Mockowanie zachowania save - sprawdzamy co Service próbuje zapisać w bazie
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            
            // Weryfikacja
            assertThat(savedUser.getWallets()).hasSize(1);
            
            // 👇 KLUCZOWA ZMIANA: getCurrencyCode() zamiast getCurrency()
            assertThat(savedUser.getWallets().get(0).getCurrencyCode()).isEqualTo("PLN");
            
            // Sprawdzenie salda 0.00
            assertThat(savedUser.getWallets().get(0).getBalance()).isEqualByComparingTo(BigDecimal.ZERO);

            return user; // Zwracamy mockowanego usera
        });

        // When
        authService.register(registerDto);

        // Then
        verify(userRepository).save(any(User.class));
    }
}