import React, { useContext } from 'react';
import { ActivityIndicator, View } from 'react-native';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import type { RootStackParamList } from '@/types';

import { AuthProvider, AuthContext } from '@/context/AuthContext';
import LoginScreen from '@/screens/LoginScreen';
import RegisterScreen from '@/screens/RegisterScreen';
import WalletScreen from '@/screens/WalletScreen';
import ExchangeScreen from '@/screens/ExchangeScreen';
import TopUpScreen from '@/screens/TopUpScreen';
import HistoryScreen from '@/screens/HistoryScreen';
import ExchangeRateChartsScreen from '@/screens/ExchangeRateChartsScreen';

const queryClient = new QueryClient();
const Stack = createNativeStackNavigator<RootStackParamList>();

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
        //  --- UŻYTKOWNIK NIEZALOGOWANY ---
        <>
          <Stack.Screen 
            name="Login" 
            component={LoginScreen} 
            options={{ headerShown: false }} 
          />
          <Stack.Screen 
            name="Register" 
            component={RegisterScreen} 
            options={{ 
              title: 'Rejestracja',
              headerBackTitle: 'Wróć',
            }} 
          />
        </>
      ) : (
        //  --- UŻYTKOWNIK ZALOGOWANY ---
        <>
          <Stack.Screen 
            name="Wallet" 
            component={WalletScreen} 
            options={{ 
              title: 'Mój Kantor',
              headerBackVisible: false 
            }}
          />
          <Stack.Screen 
            name="Exchange" 
            component={ExchangeScreen} 
            options={{ 
              title: 'Wymiana Walut',
              presentation: 'card' 
            }}
          />
          <Stack.Screen 
            name="TopUp" 
            component={TopUpScreen} 
            options={{ 
              title: 'Doładowanie PayPal',
              headerBackTitle: 'Portfel',
            }}
          />
          {/* 2. DODANA TRASA HISTORII */}
          <Stack.Screen 
            name="History" 
            component={HistoryScreen} 
            options={{ 
              title: 'Historia Transakcji',
              headerBackTitle: 'Portfel',
            }}
          />
          {/* 3. DODANA TRASA WYKRESÓW */}
          <Stack.Screen 
            name="Charts" 
            component={ExchangeRateChartsScreen} 
            options={{ 
              title: 'Wykresy Kursów',
              headerBackTitle: 'Portfel',
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