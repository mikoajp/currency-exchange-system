package pl.aeh.currencyexchange.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for error responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDto {

    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> validationErrors;

    public ErrorResponseDto(Integer status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
