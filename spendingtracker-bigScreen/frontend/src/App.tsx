import React, { useState } from 'react';
import './App.css';
import { Dashboard } from './components/Dashboard/Dashboard';
import { TransactionForm } from './components/forms/TransactionForm';
import { TransactionList } from './components/reports/TransactionList';
import { useTransactions } from './hooks/useTransactions';
import { Button } from './components/common/Button';
import { TransactionRequest } from './types/transaction';

// Navigation types
type ActiveView = 'dashboard' | 'transactions' | 'analytics' | 'categories' | 'settings';

function App() {
  const [activeView, setActiveView] = useState<ActiveView>('dashboard');
  const [showQuickAdd, setShowQuickAdd] = useState(false);
  
  const {
    transactions,
    categories,
    loading,
    error,
    addTransaction
  } = useTransactions();

  const handleAddTransaction = async (transactionRequest: TransactionRequest) => {
    try {
      await addTransaction(transactionRequest);
      setShowQuickAdd(false); // Close quick add form after successful submission
      // Transaction added successfully - state updated automatically
    } catch (err) {
      alert('Failed to add transaction. Please try again.');
    }
  };

  const handleQuickAdd = () => {
    setShowQuickAdd(true);
    setActiveView('transactions'); // Switch to transactions view when quick add is clicked
  };

  const handleViewChange = (view: ActiveView) => {
    setActiveView(view);
    setShowQuickAdd(false); // Close quick add when switching views
  };

  // Enhanced sidebar component with navigation
  const AppSidebar: React.FC = () => {
    const menuItems = [
      { id: 'dashboard' as ActiveView, icon: '🏠', label: 'Dashboard' },
      { id: 'transactions' as ActiveView, icon: '💳', label: 'Transactions' },
      { id: 'analytics' as ActiveView, icon: '📊', label: 'Analytics' },
      { id: 'categories' as ActiveView, icon: '🏷️', label: 'Categories' },
      { id: 'settings' as ActiveView, icon: '⚙️', label: 'Settings' },
    ];

    return (
      <div className="bg-white h-screen w-64 shadow-sm border-r border-gray-200 fixed left-0 top-0 z-10">
        <div className="p-6">
          {/* Logo */}
          <div className="flex items-center space-x-3 mb-8">
            <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
              <span className="text-white font-bold text-lg">S</span>
            </div>
            <span className="text-xl font-bold text-gray-900">SpendTracker</span>
          </div>
          
          {/* Quick Add Button */}
          <Button 
            onClick={handleQuickAdd}
            className="w-full mb-8"
            variant="primary"
          >
            + Quick Add
          </Button>
          
          {/* Navigation Menu */}
          <nav className="space-y-2">
            {menuItems.map((item) => (
              <button
                key={item.id}
                onClick={() => handleViewChange(item.id)}
                className={`w-full flex items-center space-x-3 px-4 py-3 rounded-lg transition-colors text-left ${
                  activeView === item.id 
                    ? 'bg-blue-50 text-blue-600 font-medium' 
                    : 'text-gray-600 hover:bg-gray-50'
                }`}
              >
                <span className="text-lg">{item.icon}</span>
                <span>{item.label}</span>
              </button>
            ))}
          </nav>
        </div>
      </div>
    );
  };

  // Quick Add Modal Component
  const QuickAddModal: React.FC = () => {
    if (!showQuickAdd) return null;

    return (
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
        <div className="bg-white rounded-2xl p-6 w-full max-w-md mx-4 shadow-2xl">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-bold text-gray-900">Quick Add Transaction</h2>
            <button
              onClick={() => setShowQuickAdd(false)}
              className="text-gray-500 hover:text-gray-700 text-2xl"
            >
              ×
            </button>
          </div>
          
          <TransactionForm
            onSubmit={handleAddTransaction}
            categories={categories}
          />
        </div>
      </div>
    );
  };

  // Error boundary component
  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="bg-white rounded-2xl p-8 shadow-sm border border-gray-200 max-w-md mx-4">
          <div className="text-center">
            <div className="text-red-600 text-4xl mb-4">⚠️</div>
            <h2 className="text-xl font-bold text-gray-900 mb-2">Something went wrong</h2>
            <p className="text-gray-600 mb-4">Error: {error}</p>
            <Button 
              onClick={() => window.location.reload()} 
              variant="primary"
            >
              Reload Page
            </Button>
          </div>
        </div>
      </div>
    );
  }

  // Render different views based on activeView
  const renderMainContent = () => {
    switch (activeView) {
      case 'dashboard':
        return <Dashboard />;
        
      case 'transactions':
        return (
          <div className="space-y-6">
            <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
              <h2 className="text-xl font-bold text-gray-900 mb-4">Add New Transaction</h2>
              <TransactionForm
                onSubmit={handleAddTransaction}
                categories={categories}
              />
            </div>
            
            {loading && (
              <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
                <div className="flex items-center justify-center">
                  <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                  <span className="ml-2 text-gray-600">Loading transactions...</span>
                </div>
              </div>
            )}
            
            <TransactionList
              transactions={transactions}
              title="This Week's Expenses"
            />
          </div>
        );
        
      case 'analytics':
        return (
          <div className="bg-white rounded-2xl p-8 shadow-sm border border-gray-100">
            <div className="text-center">
              <div className="text-6xl mb-4">📊</div>
              <h2 className="text-2xl font-bold text-gray-900 mb-2">Analytics Coming Soon</h2>
              <p className="text-gray-600">Advanced spending analytics and insights will be available here.</p>
            </div>
          </div>
        );
        
      case 'categories':
        return (
          <div className="bg-white rounded-2xl p-8 shadow-sm border border-gray-100">
            <h2 className="text-2xl font-bold text-gray-900 mb-6">Manage Categories</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {categories.map((category, index) => (
                <div key={index} className="p-4 border border-gray-200 rounded-lg">
                  <div className="flex items-center justify-between">
                    <span className="font-medium text-gray-900">{category}</span>
                    <span className="text-2xl">🏷️</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        );
        
      case 'settings':
        return (
          <div className="bg-white rounded-2xl p-8 shadow-sm border border-gray-100">
            <div className="text-center">
              <div className="text-6xl mb-4">⚙️</div>
              <h2 className="text-2xl font-bold text-gray-900 mb-2">Settings</h2>
              <p className="text-gray-600">App settings and preferences will be available here.</p>
            </div>
          </div>
        );
        
      default:
        return <Dashboard />;
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Sidebar */}
      <AppSidebar />
      
      {/* Main Content */}
      <main className="ml-64 min-h-screen">
        {/* Header for non-dashboard views */}
        {activeView !== 'dashboard' && (
          <div className="bg-white border-b border-gray-200 px-8 py-6">
            <div className="max-w-7xl mx-auto">
              <h1 className="text-3xl font-bold text-gray-900 capitalize">
                {activeView}
              </h1>
              <p className="text-gray-600 mt-1">
                {activeView === 'transactions' && 'Manage and view your transactions'}
                {activeView === 'analytics' && 'Analyze your spending patterns'}
                {activeView === 'categories' && 'Organize your expense categories'}
                {activeView === 'settings' && 'Configure your preferences'}
              </p>
            </div>
          </div>
        )}
        
        {/* Page Content */}
        <div className={`${activeView !== 'dashboard' ? 'p-8' : ''}`}>
          <div className={activeView !== 'dashboard' ? 'max-w-7xl mx-auto' : ''}>
            {renderMainContent()}
          </div>
        </div>
      </main>
      
      {/* Quick Add Modal */}
      <QuickAddModal />
    </div>
  );
}

export default App;