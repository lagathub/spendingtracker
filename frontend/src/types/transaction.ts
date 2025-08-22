export interface Transaction {
    id: number;
    amount: number;
    categoryName: string;
    note?: string;
    createdAt: string;
}

export interface Category {
    id: number;
    name: string;
    description?: string;
    }

export interface TransactionRequest {
    amount: number;
    categoryName: string;
    note?: string;
}