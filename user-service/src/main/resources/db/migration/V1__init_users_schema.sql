-- User Service Database Schema

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       merchant_name VARCHAR(255) NOT NULL,
                       merchant_category VARCHAR(50) NOT NULL,
                       country VARCHAR(3) NOT NULL,
                       stripe_customer_id VARCHAR(255) UNIQUE,
                       role VARCHAR(20) NOT NULL DEFAULT 'MERCHANT',
                       created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_created_at ON users(created_at);

ALTER TABLE users ADD CONSTRAINT chk_merchant_category
    CHECK (merchant_category IN ('RETAIL', 'DIGITAL', 'TRAVEL', 'FOOD', 'SERVICES', 'OTHER'));

ALTER TABLE users ADD CONSTRAINT chk_role
    CHECK (role IN ('MERCHANT', 'ADMIN'));