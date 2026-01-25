package pl.aeh.currencyexchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.aeh.currencyexchange.dto.WalletDto;
import pl.aeh.currencyexchange.exception.InvalidCredentialsException;
import pl.aeh.currencyexchange.exception.ResourceNotFoundException;
import pl.aeh.currencyexchange.model.User;
import pl.aeh.currencyexchange.model.Wallet;
import pl.aeh.currencyexchange.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<WalletDto> getUserWallets() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            throw new InvalidCredentialsException("No authenticated user found");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return user.getWallets().stream()
                .map(this::mapToWalletDto)
                .collect(Collectors.toList());
    }

    private WalletDto mapToWalletDto(Wallet wallet) {
        return WalletDto.builder()
                .id(wallet.getId())
                .currency(wallet.getCurrency())
                .balance(wallet.getBalance())
                .build();
    }
}
