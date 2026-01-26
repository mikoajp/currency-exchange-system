package pl.aeh.currencyexchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.aeh.currencyexchange.dto.AuthResponseDto;
import pl.aeh.currencyexchange.dto.LoginDto;
import pl.aeh.currencyexchange.dto.RegisterDto;
import pl.aeh.currencyexchange.dto.UserDto;
import pl.aeh.currencyexchange.exception.InvalidCredentialsException;
import pl.aeh.currencyexchange.exception.ResourceNotFoundException;
import pl.aeh.currencyexchange.exception.UserAlreadyExistsException;
import pl.aeh.currencyexchange.model.User;
import pl.aeh.currencyexchange.model.UserRole;
import pl.aeh.currencyexchange.model.Wallet;
import pl.aeh.currencyexchange.repository.UserRepository;
import pl.aeh.currencyexchange.security.JwtUtil;

import java.math.BigDecimal;

/**
 * Service for authentication operations (register, login)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * Register a new user
     *
     * @param registerDto registration data
     * @return authentication response with JWT token
     */
    @Transactional
    public AuthResponseDto register(RegisterDto registerDto) {
        log.debug("Attempting to register user with email: {}", registerDto.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            log.warn("Registration failed: User with email {} already exists", registerDto.getEmail());
            throw new UserAlreadyExistsException("User with email " + registerDto.getEmail() + " already exists");
        }

        // Create new user
        User user = User.builder()
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .role(UserRole.USER)
                .enabled(true)
                .build();

        // Create default PLN wallet with 0 balance
        Wallet plnWallet = Wallet.builder()
                .user(user) // Wiążemy portfel z użytkownikiem
                .currency("PLN") // ZMIANA: currency -> currencyCode (zgodnie z Wallet.java)
                .balance(BigDecimal.ZERO) // Ustawiamy 0 na start
                .build();
        
        // Zakładam, że w klasie User masz metodę addWallet, która dodaje do listy i ustawia relację
        // Jeśli nie masz, upewnij się, że User ma listę portfeli i CascadeType.ALL
        if (user.getWallets() != null) {
            user.addWallet(plnWallet);
        } else {
             // Fallback jeśli metoda addWallet nie istnieje lub lista jest null
             // Wymagałoby to wstrzyknięcia WalletRepository, ale trzymajmy się Twojej struktury Usera
             // user.setWallets(new ArrayList<>(List.of(plnWallet)));
             user.addWallet(plnWallet); 
        }

        // Save user (Cascade should save wallet too)
        user = userRepository.save(user);
        log.info("User registered successfully with email: {}", user.getEmail());

        // Generate JWT token
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();

        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponseDto(
                token,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                jwtUtil.getExpirationTime()
        );
    }

    /**
     * Login user
     *
     * @param loginDto login credentials
     * @return authentication response with JWT token
     */
    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginDto loginDto) {
        log.debug("Attempting to login user with email: {}", loginDto.getEmail());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getEmail(),
                            loginDto.getPassword()
                    )
            );

            // Get user details
            User user = userRepository.findByEmail(loginDto.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + loginDto.getEmail()));

            // Check if user is enabled
            if (!user.getEnabled()) {
                log.warn("Login failed: User account is disabled for email: {}", loginDto.getEmail());
                throw new InvalidCredentialsException("User account is disabled");
            }

            // Generate JWT token
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            log.info("User logged in successfully with email: {}", user.getEmail());

            return new AuthResponseDto(
                    token,
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    jwtUtil.getExpirationTime()
            );

        } catch (Exception e) {
            log.error("Login failed for email {}: {}", loginDto.getEmail(), e.getMessage());
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }

    /**
     * Get currently authenticated user
     *
     * @return user DTO
     */
    @Transactional(readOnly = true)
    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            throw new InvalidCredentialsException("No authenticated user found");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return mapToUserDto(user);
    }

    /**
     * Get user by ID
     *
     * @param userId user ID
     * @return user DTO
     */
    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return mapToUserDto(user);
    }

    /**
     * Map User entity to UserDto
     */
    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }
}