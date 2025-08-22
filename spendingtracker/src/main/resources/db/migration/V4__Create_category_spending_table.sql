CREATE TABLE category_spending (
    -- Primary key
    id BIGSERIAL PRIMARY KEY,
    
    -- Link to weekly report
    weekly_report_id BIGINT NOT NULL,
    
    -- Link to category
    category_id BIGINT NOT NULL,
    
    -- Amount spent in this category this week
    amount_spent DECIMAL(12,2) NOT NULL DEFAULT 0,
    
    -- Number of transactions in this category this week
    transaction_count INTEGER NOT NULL DEFAULT 0,
    
    -- Average transaction amount for this category this week
    average_transaction DECIMAL(12,2) GENERATED ALWAYS AS 
        (CASE WHEN transaction_count > 0 THEN amount_spent / transaction_count ELSE 0 END) STORED,
    
    -- Foreign key constraints
    CONSTRAINT fk_category_spending_report 
        FOREIGN KEY (weekly_report_id) REFERENCES weekly_reports(id) ON DELETE CASCADE,
    
    CONSTRAINT fk_category_spending_category 
        FOREIGN KEY (category_id) REFERENCES categories(id),
    
    -- Ensure each category appears only once per weekly report
    CONSTRAINT uk_category_spending_unique 
        UNIQUE (weekly_report_id, category_id),
    
    -- Data validation
    CONSTRAINT chk_amount_spent_non_negative CHECK (amount_spent >= 0),
    CONSTRAINT chk_transaction_count_non_negative CHECK (transaction_count >= 0)
);

-- Fast queries by weekly report
CREATE INDEX idx_category_spending_report ON category_spending(weekly_report_id);

-- Fast queries by category
CREATE INDEX idx_category_spending_category ON category_spending(category_id);

-- Composite index for common queries (report + category)
CREATE INDEX idx_category_spending_report_category 
ON category_spending(weekly_report_id, category_id);