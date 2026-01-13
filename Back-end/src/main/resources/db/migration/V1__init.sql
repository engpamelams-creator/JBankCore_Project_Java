CREATE TABLE tb_users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE tb_wallets (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    balance DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    version BIGINT,
    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES tb_users(id)
);

CREATE TABLE tb_transactions (
    id UUID PRIMARY KEY,
    sender_wallet_id UUID NOT NULL,
    receiver_wallet_id UUID NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    CONSTRAINT fk_tx_sender FOREIGN KEY (sender_wallet_id) REFERENCES tb_wallets(id),
    CONSTRAINT fk_tx_receiver FOREIGN KEY (receiver_wallet_id) REFERENCES tb_wallets(id)
);

CREATE TABLE tb_pix_keys (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    key_value VARCHAR(100) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pix_user FOREIGN KEY (user_id) REFERENCES tb_users(id)
);

CREATE INDEX idx_pix_key_unique ON tb_pix_keys(key_value);
