-- Payment Service Database Schema

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE transactions (
                              id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                              merchant_id UUID NOT NULL,
                              stripe_payment_id VARCHAR(255) UNIQUE,
                              amount BIGINT NOT NULL,
                              currency VARCHAR(3) NOT NULL,
                              status VARCHAR(20) NOT NULL,
                              fraud_score DECIMAL(4,3),
                              fraud_decision VARCHAR(20),
                              customer_email VARCHAR(255) NOT NULL,
                              description TEXT,
                              metadata JSONB,
                              created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                              updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE refunds (
                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                         transaction_id UUID NOT NULL,
                         stripe_refund_id VARCHAR(255) UNIQUE,
                         amount BIGINT NOT NULL,
                         reason VARCHAR(255),
                         status VARCHAR(20) NOT NULL,
                         created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE webhook_events (
                                id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                stripe_event_id VARCHAR(255) NOT NULL UNIQUE,
                                event_type VARCHAR(100) NOT NULL,
                                processed BOOLEAN NOT NULL DEFAULT FALSE,
                                payload TEXT,
                                created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_transactions_merchant ON transactions(merchant_id);
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);
CREATE INDEX idx_refunds_transaction ON refunds(transaction_id);
CREATE INDEX idx_webhook_events_stripe_id ON webhook_events(stripe_event_id);