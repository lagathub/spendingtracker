import { useState, useEffect } from "react";
import { Transaction, TransactionRequest } from '../types/transaction';
import { transactionService } from "../services/transactionService";

export const useTransactions = () => {
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [categories, setCategories] = useState<string[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    //loads initial data
    useEffect(() => {
        loadTransactions();
        loadCategories();
    }, []);

    const loadTransactions = async () => {
        try {
            setLoading(true);
            const data = await transactionService.getCurrentWeekTransactions();
            setTransactions(data);
        } catch (err) {
            setError('Failed to load transactions');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const loadCategories = async () => {
        try {
            const cats = await transactionService.getCategories();
            setCategories(cats);
        } catch (err) {
            console.error('Failed to load categories:', err);
        }
    };

    const addTransaction = async (transactionRequest: TransactionRequest) => {
        try {
            setLoading(true);
            const newTransaction = await transactionService.addTransaction(transactionRequest);

            //Add to local state immediately for responsive UI
            setTransactions(prev => [newTransaction, ...prev]);

            //Update categories if new category was used
            if (!categories.includes(transactionRequest.categoryName)) {
                setCategories(prev => [...prev, transactionRequest.categoryName]);
            }

            return newTransaction;
        } catch (err) {
            setError('Failed to add transaction');
            console.error(err);
            throw err;
        } finally {
            setLoading(false);
        }
        };

        return {
            transactions,
            categories,
            loading,
            error,
            addTransaction,
            refreshTransactions: loadTransactions
        };
    };