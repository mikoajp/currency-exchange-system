import React, { useContext, useCallback } from 'react';
import { 
  View, 
  Text, 
  StyleSheet, 
  FlatList, 
  TouchableOpacity, 
  ActivityIndicator, 
  RefreshControl
} from 'react-native';
import { useQuery } from '@tanstack/react-query';
import { useNavigation, useFocusEffect } from '@react-navigation/native';

import { AuthContext } from '@/context/AuthContext';
import { walletService } from '@/services/walletService';
import { Wallet } from '@/types'; 

const WalletScreen = () => {
  const { logout } = useContext(AuthContext);
  const navigation = useNavigation<any>();

  const { data: wallets, isLoading, refetch } = useQuery({
    queryKey: ['wallets'],
    queryFn: walletService.getMyWallets,
  });

  useFocusEffect(
    useCallback(() => {
      refetch();
    }, [refetch])
  );

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

      {/* Lista Portfeli */}
      <FlatList
        data={wallets}
        keyExtractor={(item) => (item.id ? item.id.toString() : item.currency)}
        renderItem={renderWalletItem}
        contentContainerStyle={styles.list}
        refreshControl={
          <RefreshControl refreshing={isLoading} onRefresh={refetch} />
        }
        ListEmptyComponent={
          <View style={styles.center}>
             <Text style={styles.emptyText}>Brak środków.</Text>
             <Text style={styles.emptyText}>Skontaktuj się z administratorem.</Text>
          </View>
        }
      />

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

        {/* PRZYCISK: Wykresy Kursów */}
        <TouchableOpacity 
          style={[styles.exchangeButton, styles.chartsButton]}
          onPress={() => navigation.navigate('ExchangeRateCharts')}
        >
          <Text style={styles.buttonText}>📊 Wykresy Kursów</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f2f2f7' },
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
  }
});

export default WalletScreen;