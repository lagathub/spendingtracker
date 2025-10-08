import api from './api';
import { Transaction, TransactionRequest } from '../types/transaction';

export const transactionService = {
    //Add new transaction
    addTransaction: async (transaction: TransactionRequest): Promise<Transaction> => {
        const response = await api.post('/api/spending/transactions', transaction);
        return response.data;
    },

    //Get current week transactions
    getCurrentWeekTransactions: async (): Promise<Transaction[]> => {
        const response = await api.get('/api/spending/current-week');
        return response.data;
    },

    //Get all transactions (for testing)
    getCategories: async (): Promise<string[]> => {
        const response = await api.get('/api/spending/categories');
        return response.data.map((cat: any) => cat.name);
    }
};