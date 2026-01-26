package pl.aeh.currencyexchange.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "exchange_rates",
    uniqueConstraints = @UniqueConstraint(name = "uk_currency_date", columnNames = {"currency", "rate_date"}),
    indexes = {
        @Index(name = "idx_exchange_rates_currency", columnList = "currency"),
        @Index(name = "idx_exchange_rates_date", columnList = "rate_date")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Currency code is required")
    @Size(min = 3, max = 3, message = "Currency code must be 3 characters (ISO 4217)")
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @NotNull(message = "Rate date is required")
    @Column(name = "rate_date", nullable = false)
    private LocalDate rateDate;

    @NotNull(message = "Bid rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Bid rate must be positive")
    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal bid;

    @NotNull(message = "Ask rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Ask rate must be positive")
    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal ask;

    @Size(max = 100)
    @Column(name = "currency_name", length = 100)
    private String currencyName;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Calculate mid rate (average of bid and ask)
     */
    public BigDecimal getMidRate() {
        return bid.add(ask).divide(BigDecimal.valueOf(2), 6, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Calculate spread (difference between ask and bid)
     */
    public BigDecimal getSpread() {
        return ask.subtract(bid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExchangeRate)) return false;
        ExchangeRate that = (ExchangeRate) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "id=" + id +
                ", currency='" + currency + '\'' + // ZMIANA w toString
                ", currencyName='" + currencyName + '\'' +
                ", rateDate=" + rateDate +
                ", bid=" + bid +
                ", ask=" + ask +
                ", midRate=" + getMidRate() +
                '}';
    }
}