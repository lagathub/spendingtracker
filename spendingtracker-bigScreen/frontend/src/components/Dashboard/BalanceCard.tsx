import React from 'react';
import { TrendData } from '../../types/dashboard';

interface BalanceCardProps {
  totalSpent: number;
  trendData: TrendData;
}

export const BalanceCard: React.FC<BalanceCardProps> = ({ totalSpent, trendData }) => {
  const formatCurrency = (amount: number): string => {
    return new Intl.NumberFormat('en-KE', {
      style: 'currency',
      currency: 'KES',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(amount).replace('KES', 'KSh');
  };

  const getTrendIcon = () => {
    return trendData.direction === 'up' ? '↗️' : '↘️';
  };

  const getTrendColor = () => {
    return trendData.direction === 'up' ? 'text-red-500' : 'text-green-500';
  };

  return (
    <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
      <div className="flex items-center justify-between mb-2">
        <span className="text-sm font-medium text-gray-500">Total Spent</span>
        <div className="w-8 h-8 bg-blue-50 rounded-full flex items-center justify-center">
          <span className="text-blue-600 text-lg">💰</span>
        </div>
      </div>
      
      <div className="mb-3">
        <div className="text-3xl font-bold text-gray-900">
          {formatCurrency(totalSpent)}
        </div>
      </div>
      
      <div className="flex items-center text-sm">
        <span className={`flex items-center ${getTrendColor()}`}>
          {getTrendIcon()} {trendData.percentage.toFixed(1)}%
        </span>
        <span className="text-gray-500 ml-1">from last month</span>
      </div>
    </div>
  );
};