CREATE SCHEMA IF NOT EXISTS ledger;

-- 1. Balance Table
CREATE TABLE IF NOT EXISTS ledger.ledger_balance (
    actor_id VARCHAR(100) NOT NULL,
    bucket VARCHAR(50) NOT NULL CONSTRAINT bucket_check CHECK (bucket IN ('USER_ESCROW', 'USER_PENDING')),
    balance BIGINT NOT NULL DEFAULT 0,
    blocked_balance BIGINT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    PRIMARY KEY (actor_id, bucket, version),
    UNIQUE (actor_id, bucket),
    CHECK (balance >= 0)
);

-- 2. Event Table (Singular Name)
CREATE TABLE IF NOT EXISTS ledger.ledger_event (
    event_id UUID PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    actor_id VARCHAR(100) NOT NULL,
    bucket VARCHAR(50) NOT NULL CHECK (bucket IN ('USER_ESCROW', 'USER_PENDING')),
    direction VARCHAR(10) NOT NULL CHECK (direction IN ('CREDIT', 'DEBIT')),
    amount BIGINT NOT NULL,
    reference VARCHAR(100),
    signature TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (actor_id, bucket, reference)
);

-- Fix: Table name must match exactly (ledger_event)
CREATE INDEX IF NOT EXISTS idx_actor_bucket ON ledger.ledger_event(actor_id, bucket);

-- 3. Settlement Table
CREATE TABLE IF NOT EXISTS ledger.settlement (
    settlement_id UUID PRIMARY KEY,
    reference_pay_event UUID NOT NULL,
    from_actor VARCHAR(100) NOT NULL,
    to_actor VARCHAR(100) NOT NULL,
    amount BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'ATTENTION_REQUIRED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP -- Comma removed here
);

-- 4. User Table
CREATE TABLE IF NOT EXISTS ledger.ledger_user(
    user_id UUID PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);