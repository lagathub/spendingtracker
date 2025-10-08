import React from 'react';
import { useDashboardData } from '../../hooks/useDashboardData';
import { BalanceCard } from './BalanceCard';
import { QuickStats } from './QuickStats';
import { WeeklyChart } from './WeeklyChart';
import { Sidebar } from './Sidebar';
import { Button } from '../common/Button';

const DashboardSkeleton: React.FC = () => (
  <div className="animate-pulse">
    <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">
      <div className="h-32 bg-gray-200 rounded-2xl"></div>
      <div className="lg:col-span-2">
        <div className="grid grid-cols-2 gap-4">
          {[1, 2, 3, 4].map((i) => (
            <div key={i} className="h-24 bg-gray-200 rounded-xl"></div>
          ))}
        </div>
      </div>
    </div>
    <div className="h-96 bg-gray-200 rounded-2xl"></div>
  </div>
);

const ErrorMessage: React.FC<{ message: string; onRetry?: () => void }> = ({ 
  message, 
  onRetry 
}) => (
  <div className="bg-red-50 border border-red-200 rounded-lg p-6 text-center">
    <div className="text-red-600 text-4xl mb-4">⚠️</div>
    <h3 className="text-lg font-semibold text-red-900 mb-2">Something went wrong</h3>
    <p className="text-red-700 mb-4">{message}</p>
    {onRetry && (
      <Button onClick={onRetry} variant="secondary">
        Try Again
      </Button>
    )}
  </div>
);

export const Dashboard: React.FC = () => {
  const { dashboardData, weeklyTrend, loading, error, refetch } = useDashboardData();

  const handleQuickAdd = () => {
    // TODO: Open quick add modal or navigate to add transaction
    console.log('Quick add clicked');
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Sidebar onQuickAdd={handleQuickAdd} />
      
      <main className="ml-64 p-8">
        <div className="max-w-7xl mx-auto">
          {/* Header */}
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-gray-900 mb-2">Dashboard</h1>
            <p className="text-gray-600">Track your spending habits and patterns</p>
          </div>

          {/* Content */}
          {loading && <DashboardSkeleton />}
          
          {error && (
            <ErrorMessage message={error} onRetry={refetch} />
          )}
          
          {!loading && !error && dashboardData && (
            <>
              {/* Top Stats Grid */}
              <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">
                <BalanceCard 
                  totalSpent={dashboardData.totalSpent}
                  trendData={dashboardData.trendData}
                />
                <div className="lg:col-span-2">
                  <QuickStats stats={dashboardData.weeklyStats} />
                </div>
              </div>
              
              {/* Chart */}
              <WeeklyChart data={weeklyTrend} />
            </>
          )}
        </div>
      </main>
    </div>
  );
};