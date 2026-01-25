import apiClient from './api';
<<<<<<< Updated upstream
import { Wallet, Transaction } from '@/types';
=======
import { Wallet, Transaction } from '../types';
>>>>>>> Stashed changes

export interface ExchangeRequest {
  fromCurrency: string;
  toCurrency: string;
  amount: number;
}

export const walletService = {
  getMyWallets: async (): Promise<Wallet[]> => {
    try {
      const response = await apiClient.get<Wallet[]>('/wallets/me');
      return response.data;
    } catch (error) {
      console.error('Error fetching wallets:', error);
      throw error;
    }
  },

  exchangeCurrency: async (data: ExchangeRequest): Promise<Transaction> => {
    try {
      const response = await apiClient.post<Transaction>('/exchange', data);
      return response.data;
    } catch (error) {
      console.error('Error exchanging currency:', error);
      throw error;
    }
  }
};