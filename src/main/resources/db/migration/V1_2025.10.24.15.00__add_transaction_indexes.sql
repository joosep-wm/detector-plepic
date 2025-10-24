-- Add indexes for optimized transaction history queries
-- This fixes the N+1 query problem and enables efficient filtering by sender and timestamp

CREATE INDEX IF NOT EXISTS idx_transaction_sender_timestamp
ON transaction(sender_id, timestamp);

CREATE INDEX IF NOT EXISTS idx_transaction_timestamp
ON transaction(timestamp);
