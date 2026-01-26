import React, { createContext, useState, useEffect, ReactNode } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import apiClient from '@/services/api'; 
import { APP_CONFIG } from '@/constants/config';

interface AuthContextType {
  isLoading: boolean;
  userToken: string | null;
  login: (email: string, pass: string) => Promise<boolean>;
  register: (name: string, email: string, pass: string) => Promise<boolean>;
  logout: () => Promise<void>;
}

export const AuthContext = createContext<AuthContextType>({} as AuthContextType);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [isLoading, setIsLoading] = useState(true);
  const [userToken, setUserToken] = useState<string | null>(null);

  const checkLoginStatus = async () => {
    try {
      const token = await AsyncStorage.getItem(APP_CONFIG.TOKEN_KEY);
      setUserToken(token);
    } catch (e) {
      console.error('Błąd odczytu tokenu', e);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    checkLoginStatus();
  }, []);

  const login = async (email: string, pass: string) => {
    setIsLoading(true);
    try {
      const response = await apiClient.post('/users/login', {
        email,
        password: pass,
      });

      const { token } = response.data; 

      if (token) {
        await AsyncStorage.setItem(APP_CONFIG.TOKEN_KEY, token);
        setUserToken(token);
        return true;
      }
      return false;
    } catch (error) {
      console.error('Błąd logowania:', error);
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  const register = async (name: string, email: string, pass: string) => {
    setIsLoading(true);
    try {
      const response = await apiClient.post('/users/register', {
        name,
        email,
        password: pass,
      });

      // --- ZMIANA DLA OPCJI BEZPIECZNEJ ---
      // Nawet jeśli API zwraca token, IGNORUJEMY go tutaj.
      // Nie ustawiamy setUserToken, aby nie przełączać ekranu automatycznie.
      // Użytkownik zostanie przekierowany do logowania przez RegisterScreen.
      
      /* if (response.data && response.data.token) {
          const { token } = response.data;
          await AsyncStorage.setItem(APP_CONFIG.TOKEN_KEY, token);
          setUserToken(token); 
      }
      */

      return true; // Sukces rejestracji
    } catch (error) {
      console.error('Błąd rejestracji:', error);
      return false; // Błąd rejestracji
    } finally {
      setIsLoading(false);
    }
  };

  const logout = async () => {
    setIsLoading(true);
    try {
        await AsyncStorage.multiRemove([
            APP_CONFIG.TOKEN_KEY,
            // APP_CONFIG.USER_KEY 
        ]);
        setUserToken(null);
    } catch (e) {
        console.error('Błąd wylogowania', e);
    } finally {
        setIsLoading(false);
    }
  };

  return (
    <AuthContext.Provider value={{ login, logout, register, isLoading, userToken }}>
      {children}
    </AuthContext.Provider>
  );
};