import apiClient from './api';
import { AuthResponse, LoginRequest, RegisterRequest } from '../types';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { APP_CONFIG } from '@/constants/config';

export const authService = {
  login: async (data: LoginRequest): Promise<AuthResponse> => {
    const response = await apiClient.post<AuthResponse>('/auth/login', data);
    if (response.data.token) {
      await AsyncStorage.setItem(APP_CONFIG.TOKEN_KEY, response.data.token);
      await AsyncStorage.setItem(APP_CONFIG.USER_KEY, JSON.stringify(response.data.user));
    }
    return response.data;
  },

  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response = await apiClient.post<AuthResponse>('/auth/register', data);
     if (response.data.token) {
      await AsyncStorage.setItem(APP_CONFIG.TOKEN_KEY, response.data.token);
      await AsyncStorage.setItem(APP_CONFIG.USER_KEY, JSON.stringify(response.data.user));
    }
    return response.data;
  },

  logout: async () => {
    await AsyncStorage.multiRemove([APP_CONFIG.TOKEN_KEY, APP_CONFIG.USER_KEY]);
  },
  
  me: async () => {
      const response = await apiClient.get('/auth/me');
      return response.data;
  }
};
