import React from 'react';
import { WeeklyStats } from '../../types/dashboard';

interface QuickStatsProps {
  stats: WeeklyStats;
}

export const QuickStats: React.FC<QuickStatsProps> = ({ stats }) => {
  const formatCurrency = (amount: number): string => {
    return new Intl.NumberFormat('en-KE', {
      style: 'currency',
      currency: 'KES',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(amount).replace('KES', 'KSh');
  };

  const statItems = [
    {
      label: 'This Week',
      value: formatCurrency(stats.spent),
      icon: '📊',
      bgColor: 'bg-green-50',
      iconColor: 'text-green-600',
    },
    {
      label: 'Transactions',
      value: stats.transactions.toString(),
      icon: '💳',
      bgColor: 'bg-purple-50',
      iconColor: 'text-purple-600',
    },
    {
      label: 'Categories',
      value: stats.categories.toString(),
      icon: '🏷️',
      bgColor: 'bg-orange-50',
      iconColor: 'text-orange-600',
    },
    {
      label: 'Daily Avg',
      value: formatCurrency(stats.dailyAverage),
      icon: '📈',
      bgColor: 'bg-blue-50',
      iconColor: 'text-blue-600',
    },
  ];

  return (
    <div className="grid grid-cols-2 gap-4">
      {statItems.map((item, index) => (
        <div key={index} className="bg-white rounded-xl p-4 shadow-sm border border-gray-100">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-medium text-gray-500 uppercase tracking-wide">
              {item.label}
            </span>
            <div className={`w-6 h-6 ${item.bgColor} rounded-full flex items-center justify-center`}>
              <span className={`${item.iconColor} text-sm`}>{item.icon}</span>
            </div>
          </div>
          <div className="text-xl font-bold text-gray-900">
            {item.value}
          </div>
        </div>
      ))}
    </div>
  );
};