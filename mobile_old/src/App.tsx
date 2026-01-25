import React, { useContext } from 'react';
import { ActivityIndicator, View } from 'react-native';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';

import { AuthProvider, AuthContext } from '@/context/AuthContext';
import LoginScreen from '@/screens/LoginScreen';
import WalletScreen from '@/screens/WalletScreen';
import ExchangeScreen from '@/screens/ExchangeScreen'; // <--- NOWY IMPORT

const queryClient = new QueryClient();
const Stack = createNativeStackNavigator();


function AppNavigator() {
  const { isLoading, userToken } = useContext(AuthContext);

  if (isLoading) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <ActivityIndicator size="large" color="#007AFF" />
      </View>
    );
  }

  return (
    <Stack.Navigator>
      {userToken == null ? (
        //  Brak tokenu -> Ekran Logowania
        <Stack.Screen 
          name="Login" 
          component={LoginScreen} 
          options={{ headerShown: false }} 
        />
      ) : (
        //  Zalogowany -> Grupa ekranów dostępnych dla użytkownika
        <>
          <Stack.Screen 
            name="Wallet" 
            component={WalletScreen} 
            options={{ 
              title: 'Mój Kantor',
              headerBackVisible: false // Ukrywa strzałkę powrotu do logowania
            }}
          />
          <Stack.Screen 
            name="Exchange" 
            component={ExchangeScreen} 
            options={{ 
              title: 'Wymiana Walut',
              presentation: 'card' // Opcjonalnie: ładna animacja wejścia
            }}
          />
        </>
      )}
    </Stack.Navigator>
  );
}

function App(): React.JSX.Element {
  return (
    <SafeAreaProvider>
      <QueryClientProvider client={queryClient}>
        <AuthProvider>
          <NavigationContainer>
            <AppNavigator />
          </NavigationContainer>
        </AuthProvider>
      </QueryClientProvider>
    </SafeAreaProvider>
  );
}

export default App;