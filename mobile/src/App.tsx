import React, { useEffect } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { Text, View, Button } from 'react-native';
import { useAuthStore } from './store/auth';
import { OwnerDashboard, ContractorDashboard, SupplierDashboard } from './screens/Dashboards';
import { LoginScreen } from './screens/Login';

const Stack = createNativeStackNavigator();

export default function App() {
  const { user, initialize } = useAuthStore();

  useEffect(() => {
    initialize();
  }, [initialize]);

  return (
    <NavigationContainer>
      <Stack.Navigator>
        {!user ? (
          <Stack.Screen name="Login" component={LoginScreen} options={{ headerShown: false }} />
        ) : user.role === 'OWNER' ? (
          <Stack.Screen name="Owner" component={OwnerDashboard} options={{ headerShown: false }} />
        ) : user.role === 'CONTRACTOR' ? (
          <Stack.Screen name="Contractor" component={ContractorDashboard} options={{ headerShown: false }} />
        ) : (
          <Stack.Screen name="Supplier" component={SupplierDashboard} options={{ headerShown: false }} />
        )}
      </Stack.Navigator>
    </NavigationContainer>
  );
}


