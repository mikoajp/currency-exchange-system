export interface User {
  id: number;
  email: string;
  firstName?: string;
  lastName?: string;
  role: 'USER' | 'ADMIN';
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName?: string; // Uwaga: w AuthContext wysyłamy 'name', upewnij się, że backend to mapuje
  lastName?: string;
}

// ZMIANA: currency -> currencyCode (zgodnie z backendem WalletDto)
export interface Wallet {
  id: number;
  currency: string; 
  balance: number;
}

// NOWOŚĆ: Potrzebne do walletService.exchangeCurrency
export interface ExchangeRequest {
  fromCurrency: string;
  toCurrency: string;
  amount: number;
}

export interface ExchangeRate {
  id: number;
  currency: string; // Pełna nazwa (np. "dolar amerykański")
  code: string;     // Kod ISO (np. "USD")
  bid: number;
  ask: number;
  midRate: number;
  rateDate: string;
  createdAt?: string;
}

export interface Transaction {
  id: number;
  type: 'DEPOSIT' | 'WITHDRAWAL' | 'BUY' | 'SELL';
  fromCurrency?: string;
  toCurrency?: string;
  fromAmount?: number;
  toAmount?: number;
  exchangeRate?: number;
  status: 'PENDING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
  createdAt: string;
}

// Navigation Types
export type RootStackParamList = {
  Login: undefined;
  Register: undefined;
  Wallet: undefined;
  Exchange: undefined;
  TopUp: undefined;
  History: undefined;
  ExchangeRateCharts: undefined;  // Screen displaying exchange rate charts
};