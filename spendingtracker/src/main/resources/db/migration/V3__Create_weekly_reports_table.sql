CREATE TABLE weekly_reports (
	-- Primary key
	id BIGSERIAL PRIMARY KEY,
	
	-- Start of the week (Monday)
	week_start_date DATE NOT NULL UNIQUE,
	
	-- End of the week (Sunday)
	week_end_date DATE NOT NULL,
	
	-- Total amount spent that week
	total_spent DECIMAL(12,2) NOT NULL DEFAULT 0,
	
	-- When this report was generated
	generated_at TIMESTAMP DEFAULT NOW(),
	
	-- Ensure week_start is always before week_end
	CONSTRAINT chk_valid_week_dates CHECK (week_start_date <= week_end_date),
	
	-- Ensure total_spent is not negative
	CONSTRAINT chk_non_negative_total CHECK (total_spent >= 0)
);

-- Fast queries by week start date
CREATE INDEX idx_weekly_reports_week_start ON weekly_reports(week_start_date);

-- Fast queries for recent reports
CREATE INDEX idx_weekly_reports_generated_at ON weekly_reports(generated_at DESC);