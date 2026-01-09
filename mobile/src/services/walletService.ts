import apiClient from './api';
import { Wallet } from '../types';

export const walletService = {
  getUserWallets: async (): Promise<Wallet[]> => {
    const response = await apiClient.get<Wallet[]>('/wallets');
    return response.data;
  },

  getWallet: async (currency: string): Promise<Wallet> => {
    const response = await apiClient.get<Wallet>(`/wallets/${currency}`);
    return response.data;
  },

  deposit: async (amount: number): Promise<Wallet> => {
    const response = await apiClient.post<Wallet>('/wallets/deposit', { amount });
    return response.data;
  }
};
