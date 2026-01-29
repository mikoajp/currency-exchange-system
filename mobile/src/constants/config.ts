import { Platform } from 'react-native';

export const API_CONFIG = {
  // Na Androidzie używamy IP maszyny hosta lub localhost z adb reverse
  BASE_URL: __DEV__ 
    ? (Platform.OS === 'android' ? 'http://localhost:8080/api' : 'http://localhost:8080/api')
    : 'https://api.currency-exchange.pl/api',
  TIMEOUT: 10000,
};

export const APP_CONFIG = {
  TOKEN_KEY: '@currency_exchange_token',
  USER_KEY: '@currency_exchange_user',
};