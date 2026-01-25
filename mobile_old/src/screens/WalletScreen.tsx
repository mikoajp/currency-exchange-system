import React, { useContext } from 'react';
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
import { AuthContext } from '@/context/AuthContext';
import { walletService } from '@/services/walletService';
import { Wallet } from '@/types'; // Import z Twojego index.ts/types.ts

const WalletScreen = ({ navigation }: any) => {
  const { logout } = useContext(AuthContext);

  // Pobieranie danych z API
  const { data: wallets, isLoading, refetch } = useQuery({
    queryKey: ['wallets'],
    queryFn: walletService.getMyWallets,
  });

  const renderWalletItem = ({ item }: { item: Wallet }) => (
    <View style={styles.card}>
      <View style={styles.row}>
        <Text style={styles.currencyCode}>{item.currency}</Text>
        <Text style={styles.balance}>
          {item.balance.toFixed(2)}
        </Text>
      </View>
    </View>
  );

  if (isLoading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color="#007AFF" />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Moje Środki</Text>
        <TouchableOpacity onPress={logout}>
          <Text style={styles.logoutText}>Wyloguj</Text>
        </TouchableOpacity>
      </View>

      <FlatList
        data={wallets}
        keyExtractor={(item) => item.id.toString()}
        renderItem={renderWalletItem}
        contentContainerStyle={styles.list}
        refreshControl={
          <RefreshControl refreshing={isLoading} onRefresh={refetch} />
        }
        ListEmptyComponent={
          <Text style={styles.emptyText}>Brak środków. Doładuj konto.</Text>
        }
      />

      {/* Przycisk przejścia do wymiany */}
      <View style={styles.footer}>
        <TouchableOpacity 
          style={styles.exchangeButton}
          onPress={() => navigation.navigate('Exchange')}
        >
          <Text style={styles.buttonText}>Wymień Walutę</Text>
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
    backgroundColor: '#fff' 
  },
  title: { fontSize: 24, fontWeight: 'bold' },
  logoutText: { color: 'red', fontSize: 16 },
  list: { padding: 16 },
  card: {
    backgroundColor: '#fff',
    padding: 20,
    borderRadius: 12,
    marginBottom: 12,
    elevation: 2,
  },
  row: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  currencyCode: { fontSize: 20, fontWeight: 'bold', color: '#333' },
  balance: { fontSize: 24, fontWeight: '600', color: '#007AFF' },
  emptyText: { textAlign: 'center', marginTop: 50, color: 'gray' },
  footer: { padding: 20, backgroundColor: '#fff' },
  exchangeButton: {
    backgroundColor: '#007AFF',
    padding: 16,
    borderRadius: 12,
    alignItems: 'center',
  },
  buttonText: { color: '#fff', fontSize: 18, fontWeight: 'bold' },
});

export default WalletScreen;