package pl.aeh.currencyexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication response (login/register)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {

    private String token;
    private String type = "Bearer";
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private Long expiresIn; // milliseconds

    public AuthResponseDto(String token, Long userId, String email, String firstName, String lastName, Long expiresIn) {
        this.token = token;
        this.type = "Bearer";
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.expiresIn = expiresIn;
    }
}
