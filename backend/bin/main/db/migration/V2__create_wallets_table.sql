CREATE TABLE wallets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    currency VARCHAR(3) NOT NULL,
    balance DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_wallets_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_currency UNIQUE (user_id, currency),
    CONSTRAINT chk_balance_positive CHECK (balance >= 0)
);

CREATE INDEX idx_wallets_user_id ON wallets(user_id);
