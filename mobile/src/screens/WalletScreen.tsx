import React, { useContext, useEffect, useState } from 'react';
import { View, Text, StyleSheet, Button, FlatList, RefreshControl, ActivityIndicator } from 'react-native';
import { AuthContext } from '@/context/AuthContext';
import { getMyWallets } from '@/services/walletService';
import { Wallet } from '@/types';

const WalletScreen = () => {
  const { logout } = useContext(AuthContext);
  const [wallets, setWallets] = useState<Wallet[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const fetchWallets = async () => {
    try {
      const data = await getMyWallets();
      setWallets(data);
    } catch (error) {
      console.error('Failed to load wallets');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useEffect(() => {
    fetchWallets();
  }, []);

  const onRefresh = () => {
    setRefreshing(true);
    fetchWallets();
  };

  const renderWalletItem = ({ item }: { item: Wallet }) => (
    <View style={styles.walletCard}>
      <Text style={styles.currencyCode}>{item.currency}</Text>
      <Text style={styles.balance}>{item.balance.toFixed(2)}</Text>
    </View>
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Twój Portfel</Text>
        <Button title="Wyloguj" onPress={logout} color="red" />
      </View>

      {loading ? (
        <ActivityIndicator size="large" color="#007AFF" style={{ marginTop: 20 }} />
      ) : (
        <FlatList
          data={wallets}
          renderItem={renderWalletItem}
          keyExtractor={(item) => item.id.toString()}
          contentContainerStyle={styles.listContainer}
          refreshControl={
            <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
          }
          ListEmptyComponent={
            <Text style={styles.emptyText}>Brak środków w portfelu.</Text>
          }
        />
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f7fb', padding: 20 },
  header: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 },
  title: { fontSize: 24, fontWeight: 'bold', color: '#333' },
  listContainer: { paddingBottom: 20 },
  walletCard: {
    backgroundColor: '#fff',
    padding: 20,
    borderRadius: 12,
    marginBottom: 15,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    elevation: 3,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
  },
  currencyCode: { fontSize: 20, fontWeight: 'bold', color: '#333' },
  balance: { fontSize: 24, fontWeight: 'bold', color: '#007AFF' },
  emptyText: { textAlign: 'center', color: '#888', marginTop: 20, fontSize: 16 },
});

export default WalletScreen;