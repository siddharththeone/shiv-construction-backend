import React from 'react';
import { View, Text, Button } from 'react-native';
import { useAuthStore } from '../store/auth';

const Container: React.FC<{ title: string }> = ({ title, children }) => (
  <View style={{ flex: 1, padding: 16 }}>
    <Text style={{ fontSize: 20, fontWeight: 'bold', marginBottom: 12 }}>{title}</Text>
    {children}
  </View>
);

export const OwnerDashboard: React.FC = () => {
  const logout = useAuthStore((s) => s.logout);
  return (
    <Container title="Owner Dashboard">
      <Text>- Sites: create, assign contractors/suppliers</Text>
      <Text>- Dashboard: progress, materials, finance</Text>
      <Button title="Logout" onPress={logout} />
    </Container>
  );
};

export const ContractorDashboard: React.FC = () => {
  const logout = useAuthStore((s) => s.logout);
  return (
    <Container title="Contractor Dashboard">
      <Text>- View assigned sites, update status, upload photos</Text>
      <Text>- Request materials</Text>
      <Button title="Logout" onPress={logout} />
    </Container>
  );
};

export const SupplierDashboard: React.FC = () => {
  const logout = useAuthStore((s) => s.logout);
  return (
    <Container title="Supplier Dashboard">
      <Text>- View material requests, update delivery status</Text>
      <Button title="Logout" onPress={logout} />
    </Container>
  );
};


