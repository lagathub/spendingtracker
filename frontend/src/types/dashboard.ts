// types/dashboard.ts

export interface TrendData {
    direction: 'up' | 'down';
    percentage: number;
}

export interface WeeklyStats {
    spent: number;
    transactions: number;
    categories: number;
    dailyAverage: number;
}

export interface DashboardSummary {
    totalSpent: number;
    trendData: TrendData;
    weeklyStats: WeeklyStats;
}

export interface WeeklyTrendData {
    weekStart: string;        // "Jan 1, 2024" format
    weekEnd: string;
    totalSpent: number;
    transactionCount: number;
    averageDaily: number;
}

export interface CategoryBreakdown {
    categoryName: string;
    amount: number;
    percentage: number;
    transactionCount: number;
}

export interface DailyBreakdown {
    date: string;             // "2024-01-15" format
    dayName: string;          // "Monday"
    amount: number;
    transactionCount: number;
}

export interface SpendingComparison {
    current: number;
    previous: number;
    percentageChange: number;
    trend: 'up' | 'down';
}