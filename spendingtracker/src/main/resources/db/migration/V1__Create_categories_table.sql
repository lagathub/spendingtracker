CREATE TABLE categories (
	-- Primary key column
	id BIGSERIAL PRIMARY KEY,
	
	-- Category name - must be unique, cannot be null
	name VARCHAR(100) UNIQUE NOT NULL,
	
	--Optional description
	description TEXT,
	
	-- Timestamp when created
	created_at TIMESTAMP DEFAULT NOW()
);

	-- Index for fast name lookups (case-insensitive)
	CREATE INDEX idx_categories_name_lower ON categories (LOWER(name));
	
	-- Insert some common categories to start with
	INSERT INTO categories (name, description) VALUES
		('Food', 'Meals, snacks, drinks'),
		('Transport', 'Matatu, boda, fuel'),
		('Airtime', 'Phone credit, data bundles'),
		('Other', 'Miscellaneous expenses');