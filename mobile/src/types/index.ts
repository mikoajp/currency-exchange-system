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
  firstName?: string;
  lastName?: string;
}

export interface Wallet {
  id: number;
  currency: string;
  balance: number;
}

export interface ExchangeRate {
  currency: string;
  currencyName: string;
  bid: number;
  ask: number;
  rateDate: string;
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
