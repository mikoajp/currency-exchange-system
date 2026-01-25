export const API_CONFIG = {
  BASE_URL: __DEV__ 
    ? 'http://localhost:8080/api' 
    : 'https://api.currency-exchange.pl/api',
  TIMEOUT: 10000,
};

export const APP_CONFIG = {
  TOKEN_KEY: '@currency_exchange_token',
  USER_KEY: '@currency_exchange_user',
};
