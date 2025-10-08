import React from 'react';
import { Button } from '../common/Button';

interface SidebarProps {
  onQuickAdd?: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ onQuickAdd }) => {
  const menuItems = [
    { icon: '🏠', label: 'Dashboard', active: true },
    { icon: '💳', label: 'Transactions', active: false },
    { icon: '📊', label: 'Analytics', active: false },
    { icon: '🏷️', label: 'Categories', active: false },
    { icon: '⚙️', label: 'Settings', active: false },
  ];

  return (
    <div className="bg-white h-screen w-64 shadow-sm border-r border-gray-200 fixed left-0 top-0">
      <div className="p-6">
        <div className="flex items-center space-x-3 mb-8">
          <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
            <span className="text-white font-bold text-lg">S</span>
          </div>
          <span className="text-xl font-bold text-gray-900">SpendTracker</span>
        </div>
        
        <Button 
          onClick={onQuickAdd}
          className="w-full mb-8"
          variant="primary"
        >
          + Quick Add
        </Button>
        
        <nav className="space-y-2">
          {menuItems.map((item, index) => (
            <a
              key={index}
              href="#"
              className={`flex items-center space-x-3 px-4 py-3 rounded-lg transition-colors ${
                item.active 
                  ? 'bg-blue-50 text-blue-600 font-medium' 
                  : 'text-gray-600 hover:bg-gray-50'
              }`}
            >
              <span className="text-lg">{item.icon}</span>
              <span>{item.label}</span>
            </a>
          ))}
        </nav>
      </div>
    </div>
  );
};