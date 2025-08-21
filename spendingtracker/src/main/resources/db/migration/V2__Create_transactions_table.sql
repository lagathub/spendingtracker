CREATE TABLE transactions (
    -- Primary key
    id BIGSERIAL PRIMARY KEY,
    
    -- Amount spent - decimal with 2 decimal places
    amount DECIMAL(12,2) NOT NULL,
    
    -- Link to categories table
    category_id BIGINT NOT NULL,
    
    -- When transaction was recorded
    created_at TIMESTAMP DEFAULT NOW(),
    
    -- Optional note
    note TEXT
);

    
    -- Link transactions to categories
    ALTER TABLE transactions 
    ADD CONSTRAINT fk_transactions_category 
    FOREIGN KEY (category_id) REFERENCES categories(id);
    
    -- Ensure amounts are positive
    ALTER TABLE transactions 
    ADD CONSTRAINT chk_positive_amount 
    CHECK (amount > 0);
    
    -- Fast queries by date range
    CREATE INDEX idx_transactions_created_at ON transactions(created_at);

	-- Fast queries by category
	CREATE INDEX idx_transactions_category ON transactions(category_id);

	-- Fast queries for small amounts (my main use case)
	CREATE INDEX idx_transactions_small_amounts 
	ON transactions(amount) 
	WHERE amount < 1000;