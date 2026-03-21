-- Notification Service Database Schema

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE notification_log (
                                  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                  type VARCHAR(20) NOT NULL,
                                  recipient VARCHAR(255) NOT NULL,
                                  subject VARCHAR(500),
                                  content TEXT,
                                  status VARCHAR(20) NOT NULL,
                                  error_message TEXT,
                                  metadata JSONB,
                                  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notification_log_type ON notification_log(type);
CREATE INDEX idx_notification_log_status ON notification_log(status);
CREATE INDEX idx_notification_log_recipient ON notification_log(recipient);
CREATE INDEX idx_notification_log_created_at ON notification_log(created_at);

ALTER TABLE notification_log ADD CONSTRAINT chk_notification_type
    CHECK (type IN ('EMAIL', 'SMS'));

ALTER TABLE notification_log ADD CONSTRAINT chk_notification_status
    CHECK (status IN ('SENT', 'FAILED', 'PENDING'));