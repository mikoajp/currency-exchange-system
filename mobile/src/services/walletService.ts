import apiClient from './api';
import { Wallet } from '@/types';

export interface ExchangeRequest {
  fromCurrency: string;
  toCurrency: string;
  amount: number;
}

export interface Transaction {
  id: number;
  fromCurrency: string;
  toCurrency: string;
  fromAmount: number;
  toAmount: number;
  exchangeRate: number;
  status: string;
  createdAt: string;
  type: string; 
}

export const walletService = {
  /**
   * Backend: GET /api/wallets/me
   */
  getMyWallets: async (): Promise<Wallet[]> => {
    try {
      const response = await apiClient.get<Wallet[]>('/wallets/me');
      return response.data;
    } catch (error) {
      console.error('Error fetching wallets:', error);
      throw error;
    }
  },

  /**
   * Backend: POST /api/wallets/topup
   */
  topUpWallet: async (amount: number): Promise<Wallet> => {
    try {
      const response = await apiClient.post<Wallet>('/wallets/topup', { amount });
      return response.data;
    } catch (error) {
      console.error('Error topping up wallet:', error);
      throw error;
    }
  },

  /**
   * Backend: POST /api/exchange
   */
  exchangeCurrency: async (data: ExchangeRequest): Promise<Transaction> => {
    try {
      const response = await apiClient.post<Transaction>('/exchange', data);
      return response.data;
    } catch (error) {
      console.error('Error exchanging currency:', error);
      throw error;
    }
  },

  /**
   * Backend: GET /api/transactions
   */
  getHistory: async (): Promise<Transaction[]> => {
    try {
      const response = await apiClient.get<Transaction[]>('/transactions');
      return response.data;
    } catch (error) {
      console.error('Error fetching history:', error);
      throw error;
    }
  }
};