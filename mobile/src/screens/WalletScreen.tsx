import React, { useContext, useCallback, useState } from 'react';
import { 
  View, 
  Text, 
  StyleSheet, 
  FlatList, 
  TouchableOpacity, 
  ActivityIndicator, 
  RefreshControl,
  ScrollView
} from 'react-native';
import { useQuery } from '@tanstack/react-query';
import { useNavigation, useFocusEffect } from '@react-navigation/native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';

import { AuthContext } from '@/context/AuthContext';
import { walletService } from '@/services/walletService';
import { exchangeRateService } from '@/services/exchangeRateService';
import { Wallet, RootStackParamList } from '@/types'; 

type WalletScreenNavigationProp = NativeStackNavigationProp<RootStackParamList, 'Wallet'>;

const WalletScreen = () => {
  const { logout } = useContext(AuthContext);
  const navigation = useNavigation<WalletScreenNavigationProp>();

  const { data: wallets, isLoading, refetch } = useQuery({
    queryKey: ['wallets'],
    queryFn: walletService.getMyWallets,
  });

  // Pobierz kursy dla wielu walut
  const { data: usdRates } = useQuery({
    queryKey: ['exchangeRates', 'USD'],
    queryFn: () => exchangeRateService.getHistoricalRates('USD', '30d'),
  });

  const { data: eurRates } = useQuery({
    queryKey: ['exchangeRates', 'EUR'],
    queryFn: () => exchangeRateService.getHistoricalRates('EUR', '30d'),
  });

  const { data: gbpRates } = useQuery({
    queryKey: ['exchangeRates', 'GBP'],
    queryFn: () => exchangeRateService.getHistoricalRates('GBP', '30d'),
  });

  const { data: chfRates } = useQuery({
    queryKey: ['exchangeRates', 'CHF'],
    queryFn: () => exchangeRateService.getHistoricalRates('CHF', '30d'),
  });

  useFocusEffect(
    useCallback(() => {
      refetch();
    }, [refetch])
  );

  const renderCurrencyRates = (currency: string, flag: string, rates: any[] | undefined) => {
    if (!rates || rates.length === 0) {
      return (
        <View key={currency} style={styles.currencySection}>
          <Text style={styles.currencyTitle}>{flag} {currency}</Text>
          <View style={styles.loadingContainer}>
            <ActivityIndicator size="small" color="#007AFF" />
            <Text style={styles.loadingText}>Ładowanie...</Text>
          </View>
        </View>
      );
    }

    return (
      <View key={currency} style={styles.currencySection}>
        <Text style={styles.currencyTitle}>{flag} {currency} ({rates.length} dni)</Text>
        {rates.map((rate: any, index: number) => {
          const rateValue = rate?.midRate || rate?.rate || 0;
          const dateValue = rate?.rateDate || rate?.date || 'Brak daty';
          return (
            <View key={index} style={styles.rateRow}>
              <Text style={styles.rateDate}>{dateValue}</Text>
              <Text style={styles.rateValue}>
                {typeof rateValue === 'number' ? rateValue.toFixed(4) : '0.0000'} PLN
              </Text>
            </View>
          );
        })}
      </View>
    );
  };

  const renderWalletItem = ({ item }: { item: Wallet }) => (
    <View style={styles.card}>
      <View style={styles.row}>
        {/* Wyświetlamy kod waluty (np. PLN) */}
        <Text style={styles.currency}>{item.currency}</Text>
        {/* Wyświetlamy saldo z 2 miejscami po przecinku */}
        <Text style={styles.balance}>
          {item.balance.toFixed(2)}
        </Text>
      </View>
    </View>
  );

  if (isLoading && !wallets) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color="#007AFF" />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      {/* Nagłówek */}
      <View style={styles.header}>
        <View>
            <Text style={styles.title}>Twój Portfel</Text>
            <Text style={styles.subtitle}>Stan konta</Text>
        </View>
        <TouchableOpacity onPress={logout}>
          <Text style={styles.logoutText}>Wyloguj</Text>
        </TouchableOpacity>
      </View>

      <ScrollView
        style={styles.scrollView}
        refreshControl={
          <RefreshControl refreshing={isLoading} onRefresh={refetch} />
        }
      >
        {/* Lista Portfeli */}
        <View style={styles.list}>
          {wallets && wallets.length > 0 ? (
            wallets.map((item) => (
              <View key={item.id ? item.id.toString() : item.currency} style={styles.card}>
                <View style={styles.row}>
                  <Text style={styles.currency}>{item.currency}</Text>
                  <Text style={styles.balance}>{item.balance.toFixed(2)}</Text>
                </View>
              </View>
            ))
          ) : (
            <View style={styles.center}>
              <Text style={styles.emptyText}>Brak środków.</Text>
              <Text style={styles.emptyText}>Skontaktuj się z administratorem.</Text>
            </View>
          )}
        </View>

        {/* Sekcja Wykresów - wszystkie waluty */}
        <View style={styles.chartsContainer}>
          <Text style={styles.mainTitle}>📊 Kursy Walut NBP</Text>
          <Text style={styles.mainSubtitle}>Ostatnie 30 dni</Text>

          {/* USD */}
          {renderCurrencyRates('USD', '🇺🇸', usdRates)}
          
          {/* EUR */}
          {renderCurrencyRates('EUR', '🇪🇺', eurRates)}
          
          {/* GBP */}
          {renderCurrencyRates('GBP', '🇬🇧', gbpRates)}
          
          {/* CHF */}
          {renderCurrencyRates('CHF', '🇨🇭', chfRates)}
        </View>
      </ScrollView>

      {/* Footer z przyciskami */}
      <View style={styles.footer}>
        {/* Przycisk Wymiany */}
        <TouchableOpacity 
          style={styles.exchangeButton}
          onPress={() => navigation.navigate('Exchange')}
        >
          <Text style={styles.buttonText}>Wymień Walutę</Text>
        </TouchableOpacity>

        {/* Przycisk Doładowania (PayPal) */}
        <TouchableOpacity 
          style={[styles.exchangeButton, styles.paypalButton]}
          onPress={() => navigation.navigate('TopUp')}
        >
          <Text style={[styles.buttonText, styles.paypalButtonText]}>Doładuj (PayPal)</Text>
        </TouchableOpacity>

        {/* PRZYCISK: Historia Transakcji */}
        <TouchableOpacity 
          style={[styles.exchangeButton, styles.historyButton]}
          onPress={() => navigation.navigate('History')}
        >
          <Text style={styles.buttonText}>Historia Transakcji</Text>
        </TouchableOpacity>

      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f2f2f7' },
  scrollView: { flex: 1 },
  center: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  header: { 
    flexDirection: 'row', 
    justifyContent: 'space-between', 
    alignItems: 'center', 
    padding: 20, 
    paddingTop: 50, 
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#e5e5ea'
  },
  title: { fontSize: 24, fontWeight: 'bold', color: '#000' },
  subtitle: { fontSize: 14, color: 'gray' },
  logoutText: { color: 'red', fontSize: 16, fontWeight: '500' },
  list: { padding: 16 },
  card: {
    backgroundColor: '#fff',
    padding: 20,
    borderRadius: 12,
    marginBottom: 12,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
  },
  row: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  currency: { fontSize: 20, fontWeight: 'bold', color: '#333' },
  balance: { fontSize: 24, fontWeight: '600', color: '#007AFF' },
  emptyText: { textAlign: 'center', color: 'gray', marginTop: 10 },
  
  footer: { 
    padding: 20, 
    backgroundColor: '#fff', 
    borderTopWidth: 1, 
    borderTopColor: '#e5e5ea', 
    paddingBottom: 30 
  },
  exchangeButton: {
    backgroundColor: '#007AFF',
    padding: 16,
    borderRadius: 12,
    alignItems: 'center',
  },
  buttonText: { color: '#fff', fontSize: 18, fontWeight: 'bold' },
  
  paypalButton: {
    backgroundColor: '#FFC439', 
    marginTop: 12,           
  },
  paypalButtonText: {
    color: '#003087',          
  },

  historyButton: {
    backgroundColor: '#8E8E93', 
    marginTop: 12,
  },

  chartsButton: {
    backgroundColor: '#5856D6',
    marginTop: 12,
  },

  chartsContainer: {
    margin: 16,
    marginTop: 0,
  },
  mainTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 4,
    textAlign: 'center',
  },
  mainSubtitle: {
    fontSize: 14,
    color: '#666',
    marginBottom: 20,
    textAlign: 'center',
  },
  currencySection: {
    backgroundColor: '#fff',
    marginBottom: 16,
    padding: 16,
    borderRadius: 12,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
  },
  currencyTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 12,
  },
  loadingContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 12,
  },
  loadingText: {
    marginLeft: 8,
    fontSize: 14,
    color: '#666',
  },
  rateRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: 8,
    borderBottomWidth: 1,
    borderBottomColor: '#e5e5ea',
  },
  rateDate: {
    fontSize: 14,
    color: '#333',
  },
  rateValue: {
    fontSize: 14,
    fontWeight: '600',
    color: '#007AFF',
  },
});

export default WalletScreen;