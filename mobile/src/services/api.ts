import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { API_CONFIG, APP_CONFIG } from '@/constants/config';

const apiClient = axios.create({
  baseURL: API_CONFIG.BASE_URL,
  timeout: API_CONFIG.TIMEOUT,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - dodaj token
apiClient.interceptors.request.use(
  async (config) => {
    const token = await AsyncStorage.getItem(APP_CONFIG.TOKEN_KEY);
    
    // --- DEBUG LOGI (Usuń przed produkcją) ---
    console.log('--- WYSYŁAM REQUEST ---');
    console.log('URL:', config.baseURL + config.url);
    console.log('Token w storage:', token);
    // ----------------------------------------

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      await AsyncStorage.multiRemove([
        APP_CONFIG.TOKEN_KEY,
        APP_CONFIG.USER_KEY,
      ]);
    }
    return Promise.reject(error);
  }
);

export default apiClient;
