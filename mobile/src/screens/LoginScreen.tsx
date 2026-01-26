import React, { useState, useContext } from 'react';
import { View, Text, TextInput, TouchableOpacity, ActivityIndicator, StyleSheet, Alert } from 'react-native';
import { useNavigation } from '@react-navigation/native'; 
import { AuthContext } from '@/context/AuthContext';

const LoginScreen = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const { login, isLoading } = useContext(AuthContext);
  const [isSubmitting, setIsSubmitting] = useState(false);
  
  const navigation = useNavigation<any>(); 

  const handleLogin = async () => {
    if (!email || !password) {
      Alert.alert('Błąd', 'Proszę wypełnić wszystkie pola');
      return;
    }

    setIsSubmitting(true);
    const success = await login(email, password);
    setIsSubmitting(false);

    if (!success) {
      Alert.alert('Niepowodzenie', 'Błędny email lub hasło.');
    }
  };

  const handleNavigateToRegister = () => {
    navigation.navigate('Register'); 
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Kantor Walutowy</Text>
      
      <View style={styles.form}>
        <Text style={styles.label}>Email</Text>
        <TextInput
          style={styles.input}
          value={email}
          onChangeText={setEmail}
          keyboardType="email-address"
          autoCapitalize="none"
          placeholder="Wprowadź email"
        />

        <Text style={styles.label}>Hasło</Text>
        <TextInput
          style={styles.input}
          value={password}
          onChangeText={setPassword}
          secureTextEntry
          placeholder="Wprowadź hasło"
        />

        <TouchableOpacity 
            style={styles.button} 
            onPress={handleLogin}
            disabled={isSubmitting || isLoading}
        >
          {isSubmitting ? (
            <ActivityIndicator color="#fff" />
          ) : (
            <Text style={styles.buttonText}>ZALOGUJ SIĘ</Text>
          )}
        </TouchableOpacity>

        {/* 4. Sekcja linku do rejestracji */}
        <View style={styles.registerContainer}>
            <Text style={styles.registerText}>Nie masz konta? </Text>
            <TouchableOpacity onPress={handleNavigateToRegister}>
                <Text style={styles.registerLink}>Zarejestruj się</Text>
            </TouchableOpacity>
        </View>

      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', padding: 20, backgroundColor: '#f5f7fb' },
  title: { fontSize: 28, fontWeight: 'bold', color: '#333', textAlign: 'center', marginBottom: 40 },
  form: { backgroundColor: '#fff', padding: 20, borderRadius: 10, elevation: 3 },
  label: { fontSize: 14, color: '#666', marginBottom: 5 },
  input: { borderWidth: 1, borderColor: '#ddd', borderRadius: 8, padding: 12, marginBottom: 15, fontSize: 16 },
  button: { backgroundColor: '#007AFF', padding: 15, borderRadius: 8, alignItems: 'center', marginTop: 10 },
  buttonText: { color: '#fff', fontWeight: 'bold', fontSize: 16 },
  registerContainer: { flexDirection: 'row', justifyContent: 'center', marginTop: 20 },
  registerText: { color: '#666', fontSize: 14 },
  registerLink: { color: '#007AFF', fontWeight: 'bold', fontSize: 14 },
});

export default LoginScreen;