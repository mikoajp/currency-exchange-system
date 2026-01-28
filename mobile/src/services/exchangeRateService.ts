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
  },

  getHistoricalRates: async (currency: string, timeRange: '7d' | '30d' | '90d'): Promise<ExchangeRate[]> => {
    const daysMap = { '7d': 7, '30d': 30, '90d': 90 };
    const days = daysMap[timeRange];
    
    const to = new Date();
    const from = new Date();
    from.setDate(from.getDate() - days);

    const fromStr = from.toISOString().split('T')[0];
    const toStr = to.toISOString().split('T')[0];

    const response = await apiClient.get<ExchangeRate[]>(`/rates/history/${currency}`, {
      params: { from: fromStr, to: toStr }
    });
    return response.data;
  }
};
