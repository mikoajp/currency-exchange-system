CREATE TABLE exchange_rates (
    id BIGSERIAL PRIMARY KEY,
    currency VARCHAR(3) NOT NULL,
    rate_date DATE NOT NULL,
    bid DECIMAL(19, 6) NOT NULL,
    ask DECIMAL(19, 6) NOT NULL,
    currency_name VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT uk_currency_date UNIQUE (currency, rate_date)
);

CREATE INDEX idx_exchange_rates_currency ON exchange_rates(currency);
CREATE INDEX idx_exchange_rates_date ON exchange_rates(rate_date DESC);
