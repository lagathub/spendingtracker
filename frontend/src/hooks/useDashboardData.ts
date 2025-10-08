import { useState, useEffect, useCallback } from 'react';
import DashboardService from '../services/dashboardService';
import { DashboardSummary, WeeklyTrendData } from '../types/dashboard';

interface UseDashboardDataReturn {
    // Data states
    dashboardData: DashboardSummary | null;
    weeklyTrend: WeeklyTrendData[];
    
    // Loading and error states
    loading: boolean;
    error: string | null;
    
    // Actions
    refetch: () => Promise<void>;
    refreshData: () => Promise<void>;
}

export const useDashboardData = (): UseDashboardDataReturn => {
    const [dashboardData, setDashboardData] = useState<DashboardSummary | null>(null);
    const [weeklyTrend, setWeeklyTrend] = useState<WeeklyTrendData[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    const fetchDashboardData = useCallback(async (): Promise<void> => {
        try {
            setLoading(true);
            setError(null);
            
            // Use the DashboardService instead of direct API calls
            const [summaryData, trendData] = await Promise.all([
                DashboardService.getDashboardSummary(),
                DashboardService.getWeeklyTrend()
            ]);
            
            setDashboardData(summaryData);
            setWeeklyTrend(trendData);
            
        } catch (err) {
            const errorMessage = err instanceof Error 
                ? err.message 
                : 'Failed to load dashboard data';
            setError(errorMessage);
            console.error('Dashboard data fetch error:', err);
        } finally {
            setLoading(false);
        }
    }, []);

    // Auto-fetch data on component mount
    useEffect(() => {
        fetchDashboardData();
    }, [fetchDashboardData]);

    const refreshData = useCallback(async (): Promise<void> => {
        await fetchDashboardData();
    }, [fetchDashboardData]);

    return { 
        dashboardData, 
        weeklyTrend, 
        loading, 
        error, 
        refetch: fetchDashboardData,
        refreshData
    };
};