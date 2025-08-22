-- View: Current week transactions with category names
CREATE VIEW current_week_transactions AS
SELECT 
    t.id,
    t.amount,
    c.name as category_name,
    t.created_at,
    t.note
FROM transactions t
JOIN categories c ON t.category_id = c.id
WHERE t.created_at >= DATE_TRUNC('week', NOW()) 
    AND t.created_at < DATE_TRUNC('week', NOW()) + INTERVAL '7 days';

-- View: Weekly spending summary with category breakdown
CREATE VIEW weekly_spending_summary AS
SELECT 
    wr.id as report_id,
    wr.week_start_date,
    wr.week_end_date,
    wr.total_spent,
    wr.generated_at,
    cs.category_id,
    c.name as category_name,
    cs.amount_spent,
    cs.transaction_count,
    cs.average_transaction,
    -- Calculate percentage of total week spending
    ROUND((cs.amount_spent / NULLIF(wr.total_spent, 0)) * 100, 2) as percentage_of_total
FROM weekly_reports wr
LEFT JOIN category_spending cs ON wr.id = cs.weekly_report_id
LEFT JOIN categories c ON cs.category_id = c.id
ORDER BY wr.week_start_date DESC, cs.amount_spent DESC;

-- View: Small transactions (under 1000 KSh) - your main focus
CREATE VIEW small_transactions AS
SELECT 
    t.id,
    t.amount,
    c.name as category_name,
    t.created_at,
    t.note,
    DATE_TRUNC('week', t.created_at) as week_start
FROM transactions t
JOIN categories c ON t.category_id = c.id
WHERE t.amount < 1000
ORDER BY t.created_at DESC;