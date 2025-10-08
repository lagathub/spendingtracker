import React from "react";
import { Transaction } from "../../types/transaction";

interface TransactionListProps {
    transactions: Transaction[];
    title: string;
}

export const TransactionList: React.FC<TransactionListProps> = ({ transactions, title }) => {
    const total = transactions.reduce((sum, t) => sum + t.amount, 0);

    return (
        <div className="transaction-list">
            <div className="list-header">
                <h3>{title}</h3>
                <span className="total">Total: KSh {total.toFixed(2)}</span>
            </div>

            {transactions.length === 0 ? (
                <p className="no-transactions">No transactions yet</p>
            ) : (
                <ul className="transactions">
                    {transactions.map(transaction => (
                        <li key={transaction.id} className="transaction-item">
                            <div className="transaction-main">
                                <span className="amount">KSh {transaction.amount.toFixed(2)}</span>
                                <span className="category">{transaction.categoryName}</span>
                            </div>
                            {transaction.note && (
                                <div className="transaction-note">{transaction.note}</div>
                            )}
                            <div className="transaction-date">
                                {new Date(transaction.createdAt).toLocaleDateString()}
                            </div>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};