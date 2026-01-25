import apiClient from './api';
import { Wallet, Transaction } from '../types';

export interface ExchangeRequest {
  fromCurrency: string;
  toCurrency: string;
  amount: number;
}

export const walletService = {
  getMyWallets: async (): Promise<Wallet[]> => {
    const response = await apiClient.get<Wallet[]>('/wallets'); 
    return response.data;
  },

  exchangeCurrency: async (data: ExchangeRequest): Promise<Transaction> => {
    const response = await apiClient.post<Transaction>('/exchange', data);
    return response.data;
  }
};