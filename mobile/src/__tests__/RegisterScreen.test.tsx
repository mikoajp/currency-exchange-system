import React from 'react';
import { render, fireEvent, waitFor } from '@testing-library/react-native';
import RegisterScreen from '../screens/RegisterScreen';
import { AuthContext } from '../context/AuthContext';

// Mock navigation
const mockNavigate = jest.fn();
jest.mock('@react-navigation/native', () => ({
  useNavigation: () => ({
    navigate: mockNavigate,
  }),
}));

// Mock Alert
jest.spyOn(require('react-native').Alert, 'alert');

describe('RegisterScreen', () => {
  const mockRegister = jest.fn();

  const renderRegisterScreen = () => {
    return render(
      <AuthContext.Provider 
        value={{ 
          register: mockRegister, 
          login: jest.fn(), 
          logout: jest.fn(), 
          isLoading: false, 
          userToken: null 
        }}
      >
        <RegisterScreen />
      </AuthContext.Provider>
    );
  };

  beforeEach(() => {
    mockRegister.mockClear();
    mockNavigate.mockClear();
    (require('react-native').Alert.alert as jest.Mock).mockClear();
  });

  it('renders correctly', () => {
    const { getByText, getByPlaceholderText } = renderRegisterScreen();
    
    expect(getByText('Rejestracja')).toBeTruthy();
    expect(getByPlaceholderText('Twoje imię')).toBeTruthy();
    expect(getByPlaceholderText('przyklad@email.com')).toBeTruthy();
    expect(getByPlaceholderText('Wprowadź hasło')).toBeTruthy();
    expect(getByPlaceholderText('Powtórz hasło')).toBeTruthy();
  });

  it('shows error if fields are empty', () => {
    const { getByText } = renderRegisterScreen();
    const registerButton = getByText('ZAREJESTRUJ SIĘ');

    fireEvent.press(registerButton);

    expect(require('react-native').Alert.alert).toHaveBeenCalledWith(
      'Błąd',
      'Proszę wypełnić wszystkie pola'
    );
    expect(mockRegister).not.toHaveBeenCalled();
  });

  it('shows error if passwords do not match', () => {
    const { getByText, getByPlaceholderText } = renderRegisterScreen();

    fireEvent.changeText(getByPlaceholderText('Twoje imię'), 'Jan Kowalski');
    fireEvent.changeText(getByPlaceholderText('przyklad@email.com'), 'jan@example.com');
    fireEvent.changeText(getByPlaceholderText('Wprowadź hasło'), 'password123');
    fireEvent.changeText(getByPlaceholderText('Powtórz hasło'), 'password456');
    
    fireEvent.press(getByText('ZAREJESTRUJ SIĘ'));

    expect(require('react-native').Alert.alert).toHaveBeenCalledWith(
      'Błąd',
      'Hasła nie są identyczne'
    );
    expect(mockRegister).not.toHaveBeenCalled();
  });

  it('calls register function with correct data', async () => {
    mockRegister.mockResolvedValue(true);
    const { getByText, getByPlaceholderText } = renderRegisterScreen();

    fireEvent.changeText(getByPlaceholderText('Twoje imię'), 'Jan Kowalski');
    fireEvent.changeText(getByPlaceholderText('przyklad@email.com'), 'jan@example.com');
    fireEvent.changeText(getByPlaceholderText('Wprowadź hasło'), 'password123');
    fireEvent.changeText(getByPlaceholderText('Powtórz hasło'), 'password123');
    
    fireEvent.press(getByText('ZAREJESTRUJ SIĘ'));

    await waitFor(() => {
      expect(mockRegister).toHaveBeenCalledWith('Jan Kowalski', 'jan@example.com', 'password123');
    });
  });

  it('shows success alert on successful registration', async () => {
    mockRegister.mockResolvedValue(true);
    const { getByText, getByPlaceholderText } = renderRegisterScreen();

    fireEvent.changeText(getByPlaceholderText('Twoje imię'), 'Jan Kowalski');
    fireEvent.changeText(getByPlaceholderText('przyklad@email.com'), 'jan@example.com');
    fireEvent.changeText(getByPlaceholderText('Wprowadź hasło'), 'password123');
    fireEvent.changeText(getByPlaceholderText('Powtórz hasło'), 'password123');
    
    fireEvent.press(getByText('ZAREJESTRUJ SIĘ'));

    await waitFor(() => {
      expect(require('react-native').Alert.alert).toHaveBeenCalledWith(
        'Sukces',
        'Konto zostało utworzone! Możesz się teraz zalogować.',
        expect.any(Array)
      );
    });
  });

  it('shows error on failed registration', async () => {
    mockRegister.mockResolvedValue(false);
    const { getByText, getByPlaceholderText } = renderRegisterScreen();

    fireEvent.changeText(getByPlaceholderText('Twoje imię'), 'Jan Kowalski');
    fireEvent.changeText(getByPlaceholderText('przyklad@email.com'), 'existing@example.com');
    fireEvent.changeText(getByPlaceholderText('Wprowadź hasło'), 'password123');
    fireEvent.changeText(getByPlaceholderText('Powtórz hasło'), 'password123');
    
    fireEvent.press(getByText('ZAREJESTRUJ SIĘ'));

    await waitFor(() => {
      expect(require('react-native').Alert.alert).toHaveBeenCalledWith(
        'Niepowodzenie',
        'Nie udało się utworzyć konta. Sprawdź, czy email nie jest już zajęty.'
      );
    });
  });
});
