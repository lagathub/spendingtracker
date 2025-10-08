import api from './api';
import { DashboardSummary, WeeklyTrendData } from '../types/dashboard';

/**
 * Dashboard-specific API service
 * Handles all dashboard-related data fetching
 */
export class DashboardService {
    
    /**
     * Get current dashboard summary (today, this week, this month totals)
     */
    static async getDashboardSummary(): Promise<DashboardSummary> {
        const response = await api.get('/api/dashboard/summary');
        return response.data;
    }
    
    /**
     * Get weekly spending trend for the last 6 weeks
     */
    static async getWeeklyTrend(): Promise<WeeklyTrendData[]> {
        const response = await api.get('/api/dashboard/weekly-trend');
        return response.data;
    }
    
    /**
     * Get today's quick stats
     */
    static async getTodayStats(): Promise<{
        todayTotal: number;
        todayTransactions: number;
        todayCategories: number;
    }> {
        const response = await api.get('/api/dashboard/today');
        return response.data;
    }
    
    /**
     * Get current week detailed breakdown
     */
    static async getCurrentWeekBreakdown(): Promise<{
        weekTotal: number;
        dailyBreakdown: Array<{
            date: string;
            amount: number;
            transactionCount: number;
        }>;
        categoryBreakdown: Array<{
            categoryName: string;
            amount: number;
            percentage: number;
        }>;
    }> {
        const response = await api.get('/api/dashboard/current-week');
        return response.data;
    }
    
    /**
     * Get spending comparison data (this week vs last week, etc.)
     */
    static async getSpendingComparison(): Promise<{
        thisWeekVsLast: {
            current: number;
            previous: number;
            percentageChange: number;
            trend: 'up' | 'down';
        };
        thisMonthVsLast: {
            current: number;
            previous: number;
            percentageChange: number;
            trend: 'up' | 'down';
        };
    }> {
        const response = await api.get('/api/dashboard/comparison');
        return response.data;
    }
}

export default DashboardService;