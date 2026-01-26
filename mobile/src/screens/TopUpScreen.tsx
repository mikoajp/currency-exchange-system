import React, { useState } from 'react';
import { 
  View, 
  Text, 
  TextInput, 
  TouchableOpacity, 
  StyleSheet, 
  Alert, 
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { walletService } from '@/services/walletService';

const TopUpScreen = () => {
  const navigation = useNavigation();
  const [amount, setAmount] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleTopUp = async () => {
    const value = parseFloat(amount.replace(',', '.'));
    if (!amount || isNaN(value) || value <= 0) {
      Alert.alert('Błąd', 'Wprowadź poprawną kwotę (np. 100)');
      return;
    }

    setIsLoading(true);

    try {
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      await walletService.topUpWallet(value);
      
      Alert.alert('Sukces', `Twoje konto zostało doładowane kwotą ${value.toFixed(2)} PLN!`, [
        { text: 'OK', onPress: () => navigation.goBack() }
      ]);
    } catch (error) {
      Alert.alert('Błąd', 'Wystąpił problem z doładowaniem. Spróbuj ponownie.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView 
      behavior={Platform.OS === "ios" ? "padding" : "height"}
      style={styles.container}
    >
      <View style={styles.content}>
        <Text style={styles.title}>Doładowanie Konta</Text>
        <Text style={styles.subtitle}>Wybierz kwotę doładowania (PLN)</Text>

        <TextInput
          style={styles.input}
          placeholder="np. 100.00"
          keyboardType="numeric"
          value={amount}
          onChangeText={setAmount}
          editable={!isLoading}
        />

        {/* Przycisk PayPal Style */}
        <TouchableOpacity 
          style={[styles.paypalButton, isLoading && styles.disabledButton]} 
          onPress={handleTopUp}
          disabled={isLoading}
        >
          {isLoading ? (
            <ActivityIndicator color="#003087" />
          ) : (
            <Text style={styles.buttonText}>Zapłać z PayPal (Sandbox)</Text>
          )}
        </TouchableOpacity>
        
        <Text style={styles.note}>
          * To jest symulator płatności (Sandbox). Żadne środki nie zostaną pobrane z Twojego konta bankowego.
        </Text>
      </View>
    </KeyboardAvoidingView>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#fff' },
  content: { flex: 1, padding: 20, justifyContent: 'center' },
  title: { fontSize: 28, fontWeight: 'bold', marginBottom: 10, textAlign: 'center', color: '#000' },
  subtitle: { fontSize: 16, color: 'gray', marginBottom: 30, textAlign: 'center' },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 8,
    padding: 15,
    fontSize: 24,
    marginBottom: 20,
    textAlign: 'center',
    fontWeight: 'bold',
    color: '#000'
  },
  paypalButton: {
    backgroundColor: '#FFC439', 
    padding: 16,
    borderRadius: 25,
    alignItems: 'center',
    marginBottom: 20,
    elevation: 3,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.2,
    shadowRadius: 2,
  },
  disabledButton: { opacity: 0.7 },
  buttonText: { color: '#003087', fontSize: 18, fontWeight: 'bold' },
  note: { color: 'gray', fontSize: 12, textAlign: 'center', marginTop: 20, fontStyle: 'italic' }
});

export default TopUpScreen;