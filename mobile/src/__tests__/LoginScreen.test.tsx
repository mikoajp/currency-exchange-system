import React from 'react';
import { render, fireEvent, waitFor } from '@testing-library/react-native';
import LoginScreen from '../screens/LoginScreen';
import { AuthContext } from '../context/AuthContext';

// Mock Alert to prevent it from blocking tests
jest.spyOn(require('react-native').Alert, 'alert');

describe('LoginScreen', () => {
  const mockLogin = jest.fn();

  const renderLoginScreen = () => {
    return render(
      <AuthContext.Provider value={{ login: mockLogin, logout: jest.fn(), isLoading: false, userToken: null }}>
        <LoginScreen />
      </AuthContext.Provider>
    );
  };

  beforeEach(() => {
    mockLogin.mockClear();
    (require('react-native').Alert.alert as jest.Mock).mockClear();
  });

  it('renders correctly', () => {
    const { getByText, getByPlaceholderText } = renderLoginScreen();
    
    expect(getByText('Kantor Walutowy')).toBeTruthy();
    expect(getByPlaceholderText('Wprowadź email')).toBeTruthy();
    expect(getByPlaceholderText('Wprowadź hasło')).toBeTruthy();
  });

  it('shows error if fields are empty', () => {
    const { getByText } = renderLoginScreen();
    const loginButton = getByText('ZALOGUJ SIĘ');

    fireEvent.press(loginButton);

    expect(require('react-native').Alert.alert).toHaveBeenCalledWith('Błąd', 'Proszę wypełnić wszystkie pola');
    expect(mockLogin).not.toHaveBeenCalled();
  });

  it('calls login function with correct data', async () => {
    mockLogin.mockResolvedValue(true);
    const { getByText, getByPlaceholderText } = renderLoginScreen();

    fireEvent.changeText(getByPlaceholderText('Wprowadź email'), 'test@example.com');
    fireEvent.changeText(getByPlaceholderText('Wprowadź hasło'), 'password123');
    fireEvent.press(getByText('ZALOGUJ SIĘ'));

    await waitFor(() => {
        expect(mockLogin).toHaveBeenCalledWith('test@example.com', 'password123');
    });
  });

  it('shows error on failed login', async () => {
    mockLogin.mockResolvedValue(false);
    const { getByText, getByPlaceholderText } = renderLoginScreen();

    fireEvent.changeText(getByPlaceholderText('Wprowadź email'), 'wrong@example.com');
    fireEvent.changeText(getByPlaceholderText('Wprowadź hasło'), 'wrongpass');
    fireEvent.press(getByText('ZALOGUJ SIĘ'));

    await waitFor(() => {
        expect(require('react-native').Alert.alert).toHaveBeenCalledWith('Niepowodzenie', 'Błędny email lub hasło.');
    });
  });
});
