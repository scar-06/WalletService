-- Create database
CREATE DATABASE wallet_db;

-- Connect to database
\c wallet_db;

-- Create wallets table
CREATE TABLE wallets (
                         id BIGSERIAL PRIMARY KEY,
                         balance BIGINT NOT NULL DEFAULT 0,
                         currency VARCHAR(3) NOT NULL DEFAULT 'USD',
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create transactions table
CREATE TABLE transactions (
                              id BIGSERIAL PRIMARY KEY,
                              wallet_id BIGINT NOT NULL REFERENCES wallets(id),
                              amount BIGINT NOT NULL,
                              type VARCHAR(10) NOT NULL,
                              description TEXT,
                              idempotency_key VARCHAR(255) UNIQUE NOT NULL,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index for faster lookups
CREATE INDEX idx_transactions_wallet_id ON transactions(wallet_id);
CREATE INDEX idx_transactions_idempotency_key ON transactions(idempotency_key);
CREATE INDEX idx_wallets_created_at ON wallets(created_at);

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger for wallets table
CREATE TRIGGER update_wallets_updated_at
    BEFORE UPDATE ON wallets
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();