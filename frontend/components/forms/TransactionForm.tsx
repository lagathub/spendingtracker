import React, { useState } from 'react';
import { TransactionRequest } from '../../types/transaction';

interface TransactionFormProps {
    onSubmit: ( transaction: TransactionRequest) => void;
    categories: string[]; //List of existing category names
}

export const TransactionForm: React.FC<TransactionFormProps> = ({ onSubmit, categories }) => {
    const [amount, setAmount] = useState<string>('');
    const [categoryName, setCategoryName] = useState<string>('');
    const [note, setNote] = useState<string>('');

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();

        if (!amount || !categoryName) {
            alert('Amount and category are required!');
            return;
        }

        const transaction: TransactionRequest = {
            amount: parseFloat(amount),
            categoryName: categoryName.trim(),
            note: note.trim() || undefined
        };

        onSubmit(transaction);

        //Reset form
        setAmount('');
        setCategoryName('');
        setNote('');
    };

    return (
        <form onSubmit={handleSubmit} className="transaction-form">
            <div className="form-group">
                <label htmlFor="amount">Amount (Ksh)</label>
                <input
                   id="amount"
                   type="number"
                   step="0.01"
                   min="0"
                   value={amount}
                   onChange={(e) => setAmount(e.target.value)}
                   placeholder="50.00"
                   autoFocus
                   />
            </div>

            <div className="form-group">
                <label htmlFor="category">Category</label>
                <input
                  id="category"
                  type="text"
                  list="categories"
                  value={categoryName}
                  onChange={(e) => setCategoryName(e.target.value)}
                  placeholder="Food, Transport, Airtime..."
                  />
                  <datalist id="categories">
                    {categories.map(cat => (
                        <option key={cat} value={cat} />
                    ))}
                  </datalist>
            </div>

            <div className="form-group">
                <label htmlFor="note">Note (Optional)</label>
                <input
                   id="note"
                   type="text"
                   value={note}
                   onChange={(e) => setNote(e.target.value)}
                   placeholder="Quick description..."
                   />
            </div>

            <button type="submit" className="submit-btn">
                Add Expense
            </button>
        </form>
    )
}