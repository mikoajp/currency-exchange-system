import apiClient from './api';
import { ExchangeRate } from '../types'; // Adjusted import path

export const exchangeRateService = {
  getCurrentRates: async (): Promise<ExchangeRate[]> => {
    const response = await apiClient.get<ExchangeRate[]>('/rates/current');
    return response.data;
  },

  getRateByCode: async (code: string): Promise<ExchangeRate> => {
    const response = await apiClient.get<ExchangeRate>(`/rates/currency/${code}`);
    return response.data;
  },

  getRateHistory: async (code: string, from?: string, to?: string): Promise<ExchangeRate[]> => {
    const response = await apiClient.get<ExchangeRate[]>(`/rates/history/${code}`, { 
      params: { from, to }
    });
    return response.data;
  }
};
