import React, { useEffect } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { Text, View, Button } from 'react-native';
import * as Notifications from 'expo-notifications';
import * as Device from 'expo-device';
import { useAuthStore } from './store/auth';
import { OwnerDashboard, ContractorDashboard, SupplierDashboard } from './screens/Dashboards';
import { LoginScreen } from './screens/Login';

const Stack = createNativeStackNavigator();

export default function App() {
  const { user, initialize } = useAuthStore();

  useEffect(() => {
    initialize();
  }, [initialize]);

  useEffect(() => {
    async function registerForPushNotificationsAsync() {
      if (Device.isDevice) {
        const { status: existingStatus } = await Notifications.getPermissionsAsync();
        let finalStatus = existingStatus;
        if (existingStatus !== 'granted') {
          const { status } = await Notifications.requestPermissionsAsync();
          finalStatus = status;
        }
        if (finalStatus !== 'granted') {
          return;
        }
        const token = (await Notifications.getExpoPushTokenAsync()).data;
        useAuthStore.getState().registerFcm(token);
      }
    }
    registerForPushNotificationsAsync();
  }, []);

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


