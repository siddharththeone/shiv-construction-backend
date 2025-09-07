import React, { useState } from 'react';
import { View, TextInput, Button, Text, ScrollView } from 'react-native';
import { useAuthStore } from '../store/auth';
import axios from 'axios';

const API_BASE = 'https://shiv-construction-backend-production.up.railway.app/api';

export const LoginScreen: React.FC = () => {
  const [isSignup, setIsSignup] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [phone, setPhone] = useState('');
  const [role, setRole] = useState('CONTRACTOR');
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const login = useAuthStore((s) => s.login);

  const onLogin = async () => {
    setError(null);
    try {
      await login({ email, password });
    } catch (e: any) {
      console.log('Login error:', e);
      setError(e?.response?.data?.error || e?.message || 'Login failed');
    }
  };

  const onSignup = async () => {
    setError(null);
    setSuccess(null);
    try {
      const response = await axios.post(`${API_BASE}/auth/signup`, {
        name,
        email,
        phone,
        password,
        role
      });
      
      if (response.data.success) {
        setSuccess('Account created successfully! You can now login.');
        setIsSignup(false);
        setEmail('');
        setPassword('');
        setName('');
        setPhone('');
      }
    } catch (e: any) {
      setError(e?.response?.data?.error || 'Signup failed');
    }
  };

  return (
    <ScrollView style={{ flex: 1, padding: 24 }}>
      <View style={{ justifyContent: 'center', gap: 12, minHeight: '100%' }}>
        <Text style={{ fontSize: 24, fontWeight: 'bold', marginBottom: 12, textAlign: 'center' }}>
          {isSignup ? 'Create Account' : 'Sign In'}
        </Text>
        
        {isSignup && (
          <>
            <TextInput 
              placeholder="Full Name" 
              value={name} 
              onChangeText={setName} 
              style={{ borderWidth: 1, padding: 12, borderRadius: 8 }} 
            />
            <TextInput 
              placeholder="Phone Number" 
              value={phone} 
              onChangeText={setPhone} 
              style={{ borderWidth: 1, padding: 12, borderRadius: 8 }} 
            />
            <View style={{ flexDirection: 'row', gap: 8 }}>
              <Button 
                title="Contractor" 
                onPress={() => setRole('CONTRACTOR')} 
                color={role === 'CONTRACTOR' ? '#007AFF' : '#ccc'} 
              />
              <Button 
                title="Supplier" 
                onPress={() => setRole('SUPPLIER')} 
                color={role === 'SUPPLIER' ? '#007AFF' : '#ccc'} 
              />
            </View>
          </>
        )}
        
        <TextInput 
          placeholder="Email" 
          value={email} 
          onChangeText={setEmail} 
          autoCapitalize="none" 
          style={{ borderWidth: 1, padding: 12, borderRadius: 8 }} 
        />
        <TextInput 
          placeholder="Password" 
          value={password} 
          onChangeText={setPassword} 
          secureTextEntry 
          style={{ borderWidth: 1, padding: 12, borderRadius: 8 }} 
        />
        
        {error ? <Text style={{ color: 'red', textAlign: 'center' }}>{error}</Text> : null}
        {success ? <Text style={{ color: 'green', textAlign: 'center' }}>{success}</Text> : null}
        
        <Button 
          title={isSignup ? 'Create Account' : 'Login'} 
          onPress={isSignup ? onSignup : onLogin} 
        />
        
        <Button 
          title={isSignup ? 'Already have an account? Sign In' : 'Need an account? Sign Up'} 
          onPress={() => {
            setIsSignup(!isSignup);
            setError(null);
            setSuccess(null);
          }} 
        />
      </View>
    </ScrollView>
  );
};


