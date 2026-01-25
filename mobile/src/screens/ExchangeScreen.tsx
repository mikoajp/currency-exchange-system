import React, { useState } from 'react';
import { 
  View, Text, TextInput, StyleSheet, TouchableOpacity, Alert, ActivityIndicator 
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
      // Po sukcesie odśwież portfele i wróć
      queryClient.invalidateQueries({ queryKey: ['wallets'] });
      Alert.alert('Sukces', `Wymieniono środki! Kurs: ${data.exchangeRate}`);
      navigation.goBack();
    },
    onError: (error: any) => {
      console.error(error);
      Alert.alert('Błąd', 'Nie udało się wykonać wymiany. Sprawdź środki.');
    }
  });

  const handleExchange = () => {
    if (!amount || isNaN(Number(amount))) {
      Alert.alert('Błąd', 'Wpisz poprawną kwotę');
      return;
    }
    
    mutation.mutate({
      fromCurrency,
      toCurrency,
      amount: parseFloat(amount)
    });
  };

  return (
    <View style={styles.container}>
      <Text style={styles.header}>Wymiana Walut</Text>

      {/* Prosty formularz - w przyszłości można dodać Dropdowny */}
      <View style={styles.inputGroup}>
        <Text style={styles.label}>Z waluty (Kod):</Text>
        <TextInput 
          style={styles.input} 
          value={fromCurrency} 
          onChangeText={setFromCurrency}
          autoCapitalize="characters"
          maxLength={3}
        />
      </View>

      <View style={styles.inputGroup}>
        <Text style={styles.label}>Na walutę (Kod):</Text>
        <TextInput 
          style={styles.input} 
          value={toCurrency} 
          onChangeText={setToCurrency} 
          autoCapitalize="characters"
          maxLength={3}
        />
      </View>

      <View style={styles.inputGroup}>
        <Text style={styles.label}>Kwota:</Text>
        <TextInput 
          style={styles.input} 
          value={amount} 
          onChangeText={setAmount} 
          keyboardType="numeric"
          placeholder="np. 100.00"
        />
      </View>

      <TouchableOpacity 
        style={styles.button} 
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
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20, backgroundColor: '#fff' },
  header: { fontSize: 24, fontWeight: 'bold', marginBottom: 30, textAlign: 'center' },
  inputGroup: { marginBottom: 20 },
  label: { fontSize: 16, marginBottom: 5, color: '#666' },
  input: { 
    borderWidth: 1, 
    borderColor: '#ddd', 
    padding: 15, 
    borderRadius: 8, 
    fontSize: 18 
  },
  button: {
    backgroundColor: '#34C759', // Zielony kolor dla akcji finansowej
    padding: 18,
    borderRadius: 12,
    alignItems: 'center',
    marginTop: 20
  },
  buttonText: { color: '#fff', fontSize: 18, fontWeight: 'bold' }
});

export default ExchangeScreen;