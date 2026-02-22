

ALTER TABLE ledger.settlement
ADD COLUMN settlement_type VARCHAR(20) NOT NULL CHECK (settlement_type IN ('FUNDING', 'PAYMENT', 'WITHDRAWAL')) DEFAULT 'PAYMENT';

