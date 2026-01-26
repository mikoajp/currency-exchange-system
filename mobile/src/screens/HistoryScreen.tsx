import React from 'react';
import { View, Text, FlatList, StyleSheet, ActivityIndicator } from 'react-native';
import { useQuery } from '@tanstack/react-query';
import { walletService, Transaction } from '@/services/walletService';

const HistoryScreen = () => {
  const { data: transactions, isLoading } = useQuery({
    queryKey: ['history'],
    queryFn: walletService.getHistory,
  });

  const renderItem = ({ item }: { item: Transaction }) => {
    const isDeposit = item.type === 'DEPOSIT';
    
    return (
      <View style={styles.card}>
        <View style={styles.row}>
          <View>
            <Text style={styles.type}>
              {isDeposit ? '💰 Doładowanie' : '💱 Wymiana'}
            </Text>
            <Text style={styles.date}>
              {new Date(item.createdAt || '').toLocaleString()}
            </Text>
          </View>
          <View style={{ alignItems: 'flex-end' }}>
            {isDeposit ? (
              <Text style={styles.amountGreen}>+{item.toAmount} {item.toCurrency}</Text>
            ) : (
              <>
                <Text style={styles.amountRed}>-{item.fromAmount} {item.fromCurrency}</Text>
                <Text style={styles.amountGreen}>+{item.toAmount} {item.toCurrency}</Text>
              </>
            )}
          </View>
        </View>
      </View>
    );
  };

  if (isLoading) {
    return <View style={styles.center}><ActivityIndicator size="large" /></View>;
  }

  return (
    <View style={styles.container}>
      <FlatList
        data={transactions}
        keyExtractor={(item) => item.id.toString()}
        renderItem={renderItem}
        contentContainerStyle={{ padding: 16 }}
        ListEmptyComponent={<Text style={styles.empty}>Brak historii transakcji</Text>}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f2f2f7' },
  center: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  card: {
    backgroundColor: '#fff',
    padding: 16,
    borderRadius: 12,
    marginBottom: 10,
    elevation: 1
  },
  row: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  type: { fontWeight: 'bold', fontSize: 16, marginBottom: 4 },
  date: { color: 'gray', fontSize: 12 },
  amountGreen: { color: 'green', fontWeight: 'bold', fontSize: 16 },
  amountRed: { color: 'red', fontSize: 14 },
  empty: { textAlign: 'center', marginTop: 50, color: 'gray' }
});

export default HistoryScreen;