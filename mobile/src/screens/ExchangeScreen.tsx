import React, { useState } from 'react';
import { 
  View, Text, TextInput, StyleSheet, TouchableOpacity, Alert, ActivityIndicator, KeyboardAvoidingView, Platform 
} from 'react-native';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { walletService } from '@/services/walletService';

const ExchangeScreen = ({ navigation }: any) => {
  const [fromCurrency, setFromCurrency] = useState('PLN');
  const [toCurrency, setToCurrency] = useState('USD');
  const [amount, setAmount] = useState('');
  
  const queryClient = useQueryClient();

  // Obsługa wysyłania transakcji
  const mutation = useMutation({
    mutationFn: walletService.exchangeCurrency,
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['wallets'] });
      
      Alert.alert(
        'Sukces', 
        `Wymieniono ${data.fromAmount} ${data.fromCurrency} na ${data.toAmount} ${data.toCurrency}.\nKurs: ${data.exchangeRate}`,
        [{ text: 'OK', onPress: () => navigation.goBack() }]
      );
    },
    onError: (error: any) => {
      console.error(error);
      Alert.alert('Błąd', 'Nie udało się wykonać wymiany. Sprawdź czy masz wystarczająco środków.');
    }
  });

  const handleExchange = () => {
    // 1. Zamiana przecinka na kropkę (dla polskich klawiatur)
    const normalizedAmount = amount.replace(',', '.');
    const value = parseFloat(normalizedAmount);

    if (!amount || isNaN(value) || value <= 0) {
      Alert.alert('Błąd', 'Wpisz poprawną kwotę');
      return;
    }
    
    if (fromCurrency.toUpperCase() === toCurrency.toUpperCase()) {
      Alert.alert('Błąd', 'Wybierz różne waluty');
      return;
    }
    
    mutation.mutate({
      fromCurrency: fromCurrency.toUpperCase(),
      toCurrency: toCurrency.toUpperCase(),
      amount: value
    });
  };

  return (
    <KeyboardAvoidingView 
      behavior={Platform.OS === "ios" ? "padding" : "height"}
      style={styles.container}
    >
      <View style={styles.content}>
        <Text style={styles.header}>Wymiana Walut</Text>

        <View style={styles.row}>
            <View style={styles.col}>
                <Text style={styles.label}>Z waluty:</Text>
                <TextInput 
                  style={styles.input} 
                  value={fromCurrency} 
                  onChangeText={setFromCurrency}
                  autoCapitalize="characters"
                  maxLength={3}
                  placeholder="PLN"
                />
            </View>
            <Text style={styles.arrow}>➡️</Text>
            <View style={styles.col}>
                <Text style={styles.label}>Na walutę:</Text>
                <TextInput 
                  style={styles.input} 
                  value={toCurrency} 
                  onChangeText={setToCurrency} 
                  autoCapitalize="characters"
                  maxLength={3}
                  placeholder="USD"
                />
            </View>
        </View>

        <View style={styles.inputGroup}>
          <Text style={styles.label}>Kwota do wymiany:</Text>
          <TextInput 
            style={[styles.input, styles.amountInput]} 
            value={amount} 
            onChangeText={setAmount} 
            keyboardType="numeric"
            placeholder="0.00"
          />
        </View>

        <TouchableOpacity 
          style={[styles.button, mutation.isPending && styles.disabled]} 
          onPress={handleExchange}
          disabled={mutation.isPending}
        >
          {mutation.isPending ? (
            <ActivityIndicator color="#fff" />
          ) : (
            <Text style={styles.buttonText}>Zatwierdź Wymianę</Text>
          )}
        </TouchableOpacity>
      </View>
    </KeyboardAvoidingView>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#fff' },
  content: { padding: 20, justifyContent: 'center', flex: 1 },
  header: { fontSize: 28, fontWeight: 'bold', marginBottom: 40, textAlign: 'center' },
  
  row: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 },
  col: { flex: 1 },
  arrow: { fontSize: 24, marginHorizontal: 10, marginTop: 15 },
  
  inputGroup: { marginBottom: 30 },
  label: { fontSize: 16, marginBottom: 8, color: '#666' },
  input: { 
    borderWidth: 1, 
    borderColor: '#ddd', 
    padding: 15, 
    borderRadius: 12, 
    fontSize: 18,
    backgroundColor: '#f9f9f9',
    textAlign: 'center',
    fontWeight: 'bold'
  },
  amountInput: {
    fontSize: 24,
    color: '#007AFF'
  },
  button: {
    backgroundColor: '#007AFF',
    padding: 18,
    borderRadius: 12,
    alignItems: 'center',
    marginTop: 10,
    elevation: 3
  },
  disabled: { opacity: 0.7 },
  buttonText: { color: '#fff', fontSize: 18, fontWeight: 'bold' }
});

export default ExchangeScreen;