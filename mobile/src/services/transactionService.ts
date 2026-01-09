import apiClient from './api';
import { Transaction } from '../types';

interface ExchangeRequest {
  fromCurrency: string;
  toCurrency: string;
  amount: number;
}

export const transactionService = {
  exchange: async (data: ExchangeRequest): Promise<Transaction> => {
    const response = await apiClient.post<Transaction>('/transactions/exchange', data);
    return response.data;
  },

  getHistory: async (page = 0, size = 20): Promise<{ content: Transaction[], totalPages: number }> => {
    const response = await apiClient.get('/transactions/history', {
      params: { page, size }
    });
    return response.data;
  }
};
