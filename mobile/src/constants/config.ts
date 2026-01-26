import { Platform } from 'react-native';

export const API_CONFIG = {
  // Na Androidzie musimy użyć 10.0.2.2, na iOS działa localhost
  BASE_URL: __DEV__ 
    ? (Platform.OS === 'android' ? 'http://10.0.2.2:8080/api' : 'http://localhost:8080/api')
    : 'https://api.currency-exchange.pl/api',
  TIMEOUT: 10000,
};

export const APP_CONFIG = {
  TOKEN_KEY: '@currency_exchange_token',
  USER_KEY: '@currency_exchange_user',
};