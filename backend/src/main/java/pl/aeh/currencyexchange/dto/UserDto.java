package pl.aeh.currencyexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.aeh.currencyexchange.model.UserRole;

import java.time.LocalDateTime;

/**
 * DTO for user information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    private Boolean enabled;
    private LocalDateTime createdAt;
}
