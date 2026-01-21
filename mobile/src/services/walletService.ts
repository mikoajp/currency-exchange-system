import apiClient from './api';
import { Wallet } from '@/types';

export const getMyWallets = async (): Promise<Wallet[]> => {
  try {
    const response = await apiClient.get<Wallet[]>('/wallets/me');
    return response.data;
  } catch (error) {
    console.error('Error fetching wallets:', error);
    throw error;
  }
};
