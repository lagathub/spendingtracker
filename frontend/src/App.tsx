import React from 'react';
import './App.css';
import { TransactionForm } from './components/forms/TransactionForm';
import { TransactionList } from './components/reports/TransactionList';
import { useTransactions } from './hooks/useTransactions';

function App() {
  const {
    transactions,
    categories,
    loading,
    error,
    addTransaction
  } = useTransactions();

const handleAddTransaction = async (transactionRequest: any) => {
  try {
    await addTransaction(transactionRequest);
    //Transaction added successfully - state updated automatically
  } catch (err) {
    alert('Failed to add transaction. Please try again.');
  }
};

if (error) {
  return (
    <div className="app">
      <div className="error">Error: {error}</div>
    </div>
  );
}

  return (
    <div className="app">
      <header className="app-header">
        <h1>SpendingTracker</h1>
      </header>

      <main className="app-main">
        <TransactionForm
          onSubmit={handleAddTransaction}
          categories={categories}
          />

          {loading && <div className="loading">Loading...</div>}
          
          <TransactionList
             transactions={transactions}
             title="This Week's Expenses"
             />
      </main>
    </div>
  );
}

export default App;
