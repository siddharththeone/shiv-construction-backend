import React, { useState } from 'react';
import { View, TextInput, Button, Text } from 'react-native';
import { useAuthStore } from '../store/auth';

export const LoginScreen: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const login = useAuthStore((s) => s.login);

  const onLogin = async () => {
    setError(null);
    try {
      await login({ email, password });
    } catch (e: any) {
      setError(e?.response?.data?.error || 'Login failed');
    }
  };

  return (
    <View style={{ flex: 1, padding: 24, justifyContent: 'center', gap: 12 }}>
      <Text style={{ fontSize: 24, fontWeight: 'bold', marginBottom: 12 }}>Sign in</Text>
      <TextInput placeholder="Email" value={email} onChangeText={setEmail} autoCapitalize="none" style={{ borderWidth: 1, padding: 12, borderRadius: 8 }} />
      <TextInput placeholder="Password" value={password} onChangeText={setPassword} secureTextEntry style={{ borderWidth: 1, padding: 12, borderRadius: 8 }} />
      {error ? <Text style={{ color: 'red' }}>{error}</Text> : null}
      <Button title="Login" onPress={onLogin} />
    </View>
  );
};


