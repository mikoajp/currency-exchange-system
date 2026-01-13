import React, { useContext } from 'react';
import { View, Text, StyleSheet, Button } from 'react-native';
import { AuthContext } from '@/context/AuthContext';

const WalletScreen = () => {
  const { logout } = useContext(AuthContext);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Twój Portfel</Text>
      <Text style={styles.subtitle}>Jesteś zalogowany!</Text>
      
      <View style={styles.balanceContainer}>
        <Text>Tu wkrótce pojawi się saldo (PLN, USD, EUR)</Text>
      </View>

      <Button title="Wyloguj się" onPress={logout} color="red" />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', alignItems: 'center', padding: 20 },
  title: { fontSize: 24, fontWeight: 'bold', marginBottom: 10 },
  subtitle: { fontSize: 16, color: 'gray', marginBottom: 30 },
  balanceContainer: { marginBottom: 30, padding: 20, backgroundColor: '#eee', borderRadius: 10 }
});

export default WalletScreen;