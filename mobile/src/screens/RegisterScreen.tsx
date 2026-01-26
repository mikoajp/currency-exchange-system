import React, { useState, useContext } from 'react';
import { View, Text, TextInput, TouchableOpacity, ActivityIndicator, StyleSheet, Alert, ScrollView } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { AuthContext } from '@/context/AuthContext';

const RegisterScreen = () => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  
  const { register, isLoading } = useContext(AuthContext); 
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigation = useNavigation<any>();

  const handleRegister = async () => {
    // --- 1. Czy wszystkie pola są wypełnione? ---
    if (!name || !email || !password || !confirmPassword) {
      Alert.alert('Błąd', 'Proszę wypełnić wszystkie pola');
      return;
    }

    // --- 2. Walidacja formatu Email ---
    // Prosty regex sprawdzający obecność znaku @ i kropki
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        Alert.alert('Błąd', 'Wprowadź poprawny adres email');
        return;
    }

    // --- 3. Walidacja długości hasła ---
    // Backend wymaga min. 6 znaków
    if (password.length < 6) {
        Alert.alert('Błąd', 'Hasło musi mieć co najmniej 6 znaków');
        return;
    }

    // --- 4. Czy hasła są identyczne? ---
    if (password !== confirmPassword) {
      Alert.alert('Błąd', 'Hasła nie są identyczne');
      return;
    }

    // --- WYSYŁKA ---
    setIsSubmitting(true);
    
    // Wywołanie rejestracji
    const success = await register(name, email, password);
    
    setIsSubmitting(false);

    if (success) {
      // Opcja bezpieczna: Wyświetlamy sukces i przenosimy do logowania
      Alert.alert('Sukces', 'Konto zostało utworzone! Możesz się teraz zalogować.', [
        { text: 'OK', onPress: () => navigation.navigate('Login') } 
      ]);
    } else {
      // Komunikat ogólny (szczegóły błędu np. 409 Conflict są logowane w konsoli)
      Alert.alert('Niepowodzenie', 'Nie udało się utworzyć konta. Sprawdź, czy email nie jest już zajęty.');
    }
  };

  const handleNavigateToLogin = () => {
    navigation.navigate('Login'); 
  };

  return (
    <ScrollView contentContainerStyle={styles.container} keyboardShouldPersistTaps="handled">
      <Text style={styles.title}>Rejestracja</Text>
      
      <View style={styles.form}>
        <Text style={styles.label}>Imię</Text>
        <TextInput
          style={styles.input}
          value={name}
          onChangeText={setName}
          placeholder="Twoje imię"
          autoCapitalize="words"
        />

        <Text style={styles.label}>Email</Text>
        <TextInput
          style={styles.input}
          value={email}
          onChangeText={setEmail}
          keyboardType="email-address"
          autoCapitalize="none"
          placeholder="przyklad@email.com"
        />

        <Text style={styles.label}>Hasło (min. 6 znaków)</Text> 
        <TextInput
          style={styles.input}
          value={password}
          onChangeText={setPassword}
          secureTextEntry
          placeholder="Wprowadź hasło"
        />

        <Text style={styles.label}>Potwierdź hasło</Text>
        <TextInput
          style={styles.input}
          value={confirmPassword}
          onChangeText={setConfirmPassword}
          secureTextEntry
          placeholder="Powtórz hasło"
        />

        <TouchableOpacity 
            style={styles.button} 
            onPress={handleRegister}
            disabled={isSubmitting || isLoading}
        >
          {isSubmitting ? (
            <ActivityIndicator color="#fff" />
          ) : (
            <Text style={styles.buttonText}>ZAREJESTRUJ SIĘ</Text>
          )}
        </TouchableOpacity>

        <View style={styles.loginContainer}>
            <Text style={styles.loginText}>Masz już konto? </Text>
            <TouchableOpacity onPress={handleNavigateToLogin}>
                <Text style={styles.loginLink}>Zaloguj się</Text>
            </TouchableOpacity>
        </View>

      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: { flexGrow: 1, justifyContent: 'center', padding: 20, backgroundColor: '#f5f7fb' },
  title: { fontSize: 28, fontWeight: 'bold', color: '#333', textAlign: 'center', marginBottom: 30, marginTop: 20 },
  form: { backgroundColor: '#fff', padding: 20, borderRadius: 10, elevation: 3 },
  label: { fontSize: 14, color: '#666', marginBottom: 5 },
  input: { borderWidth: 1, borderColor: '#ddd', borderRadius: 8, padding: 12, marginBottom: 15, fontSize: 16 },
  button: { backgroundColor: '#28a745', padding: 15, borderRadius: 8, alignItems: 'center', marginTop: 10 }, 
  buttonText: { color: '#fff', fontWeight: 'bold', fontSize: 16 },
  loginContainer: { flexDirection: 'row', justifyContent: 'center', marginTop: 20 },
  loginText: { color: '#666', fontSize: 14 },
  loginLink: { color: '#007AFF', fontWeight: 'bold', fontSize: 14 },
});

export default RegisterScreen;