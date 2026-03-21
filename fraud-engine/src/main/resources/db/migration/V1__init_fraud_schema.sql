-- Fraud Engine Database Schema

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE fraud_audit_log (
                                 id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                 transaction_id UUID NOT NULL,
                                 merchant_id UUID NOT NULL,
                                 fraud_score DECIMAL(5,4) NOT NULL,
                                 decision VARCHAR(20) NOT NULL,
                                 feature_vector JSONB,
                                 contributing_factors TEXT,
                                 latency_ms INTEGER NOT NULL,
                                 model_version VARCHAR(50) NOT NULL,
                                 created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_fraud_audit_transaction ON fraud_audit_log(transaction_id);
CREATE INDEX idx_fraud_audit_merchant ON fraud_audit_log(merchant_id);
CREATE INDEX idx_fraud_audit_decision ON fraud_audit_log(decision);
CREATE INDEX idx_fraud_audit_created_at ON fraud_audit_log(created_at);
CREATE INDEX idx_fraud_audit_score ON fraud_audit_log(fraud_score);

-- Constraint to ensure fraud_score is between 0 and 1
ALTER TABLE fraud_audit_log ADD CONSTRAINT chk_fraud_score
    CHECK (fraud_score >= 0.0 AND fraud_score <= 1.0);

ALTER TABLE fraud_audit_log ADD CONSTRAINT chk_fraud_decision
    CHECK (decision IN ('APPROVE', 'REVIEW', 'BLOCK'));