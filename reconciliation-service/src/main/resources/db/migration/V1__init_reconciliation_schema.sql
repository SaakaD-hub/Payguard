-- Reconciliation Service Database Schema

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE settlements (
                             id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                             settlement_date DATE NOT NULL UNIQUE,
                             internal_total DECIMAL(15,2) NOT NULL,
                             stripe_total DECIMAL(15,2) NOT NULL,
                             difference DECIMAL(15,2) NOT NULL,
                             transaction_count INTEGER NOT NULL,
                             status VARCHAR(20) NOT NULL,
                             notes TEXT,
                             created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE discrepancies (
                               id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                               settlement_id UUID NOT NULL,
                               transaction_id UUID NOT NULL,
                               internal_amount DECIMAL(15,2) NOT NULL,
                               stripe_amount DECIMAL(15,2),
                               difference DECIMAL(15,2) NOT NULL,
                               discrepancy_type VARCHAR(50) NOT NULL,
                               description TEXT,
                               resolved BOOLEAN NOT NULL DEFAULT FALSE,
                               created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_settlements_date ON settlements(settlement_date);
CREATE INDEX idx_settlements_status ON settlements(status);
CREATE INDEX idx_discrepancies_settlement ON discrepancies(settlement_id);
CREATE INDEX idx_discrepancies_resolved ON discrepancies(resolved);

ALTER TABLE settlements ADD CONSTRAINT chk_settlement_status
    CHECK (status IN ('MATCHED', 'DISCREPANCY', 'PENDING'));