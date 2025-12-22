import { useQuery } from '@tanstack/react-query';
import { exchangeRateService } from '../services/exchangeRateService';

export const useExchangeRates = () => {
  return useQuery({
    queryKey: ['exchangeRates'],
    queryFn: exchangeRateService.getCurrentRates,
    staleTime: 1000 * 60 * 5, // 5 minutes
  });
};

export const useRateByCode = (code: string) => {
  return useQuery({
    queryKey: ['exchangeRate', code],
    queryFn: () => exchangeRateService.getRateByCode(code),
    enabled: !!code,
  });
};

export const useRateHistory = (code: string, from?: string, to?: string) => {
  return useQuery({
    queryKey: ['rateHistory', code, from, to],
    queryFn: () => exchangeRateService.getRateHistory(code, from, to),
    enabled: !!code,
  });
};
