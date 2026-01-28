import React, { useContext, useEffect } from 'react';
import { render, waitFor, fireEvent } from '@testing-library/react-native';
import { AuthProvider, AuthContext } from '../context/AuthContext';
import AsyncStorage from '@react-native-async-storage/async-storage';
import apiClient from '../services/api';
import { APP_CONFIG } from '../constants/config';
import { Text } from 'react-native';

// Mock Dependencies
jest.mock('@react-native-async-storage/async-storage', () => ({
  getItem: jest.fn(),
  setItem: jest.fn(),
  multiRemove: jest.fn(),
}));

jest.mock('../services/api', () => ({
  post: jest.fn(),
}));

const TestComponent = () => {
  const { userToken, login, logout, isLoading } = useContext(AuthContext);
  return (
    <>
        <Text testID="token">{userToken}</Text>
        <Text testID="loading">{isLoading.toString()}</Text>
        <Text onPress={() => login('test@email.com', 'pass')} testID="loginBtn">Login</Text>
        <Text onPress={logout} testID="logoutBtn">Logout</Text>
    </>
  );
};

describe('AuthContext', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('checks login status on mount (token exists)', async () => {
    (AsyncStorage.getItem as jest.Mock).mockResolvedValue('stored-token');

    const { getByTestId } = render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    await waitFor(() => {
      const loadingElement = getByTestId('loading');
      expect(loadingElement.props.children).toBe('false');
    });
    expect(getByTestId('token').props.children).toBe('stored-token');
    expect(AsyncStorage.getItem).toHaveBeenCalledWith(APP_CONFIG.TOKEN_KEY);
  });

  it('checks login status on mount (no token)', async () => {
    (AsyncStorage.getItem as jest.Mock).mockResolvedValue(null);

    const { getByTestId } = render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    await waitFor(() => {
      const loadingElement = getByTestId('loading');
      expect(loadingElement.props.children).toBe('false');
    });
    expect(getByTestId('token').props.children).toBeFalsy();
  });

  it('login success stores token', async () => {
    (AsyncStorage.getItem as jest.Mock).mockResolvedValue(null);
    (apiClient.post as jest.Mock).mockResolvedValue({ data: { token: 'new-token' } });

    const { getByTestId } = render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );
    
    // Wait for initial check
    await waitFor(() => {
      const loadingElement = getByTestId('loading');
      expect(loadingElement.props.children).toBe('false');
    });

    const loginBtn = getByTestId('loginBtn');
    fireEvent.press(loginBtn);

    await waitFor(() => {
      expect(getByTestId('token').props.children).toBe('new-token');
    });
    expect(AsyncStorage.setItem).toHaveBeenCalledWith(APP_CONFIG.TOKEN_KEY, 'new-token');
  });

  it('logout removes token', async () => {
    (AsyncStorage.getItem as jest.Mock).mockResolvedValue('existing-token');
    
    const { getByTestId } = render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    await waitFor(() => {
      const loadingElement = getByTestId('loading');
      expect(loadingElement.props.children).toBe('false');
    });
    
    const logoutBtn = getByTestId('logoutBtn');
    fireEvent.press(logoutBtn);

    await waitFor(() => {
      expect(getByTestId('token').props.children).toBeFalsy();
    });
    expect(AsyncStorage.multiRemove).toHaveBeenCalledWith([APP_CONFIG.TOKEN_KEY]);
  });
});
