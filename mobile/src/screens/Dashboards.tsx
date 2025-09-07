import React from 'react';
import { View, Text, Button, TouchableOpacity, StyleSheet } from 'react-native';
import { useAuthStore } from '../store/auth';
import { SitesScreen } from './SitesScreen';
import { CreateSiteScreen } from './CreateSiteScreen';

const Container: React.FC<{ title: string }> = ({ title, children }) => (
  <View style={{ flex: 1, padding: 16 }}>
    <Text style={{ fontSize: 20, fontWeight: 'bold', marginBottom: 12 }}>{title}</Text>
    {children}
  </View>
);

export const OwnerDashboard: React.FC = () => {
  const logout = useAuthStore((s) => s.logout);
  const [currentScreen, setCurrentScreen] = React.useState<'dashboard' | 'sites' | 'createSite'>('dashboard');

  if (currentScreen === 'sites') {
    return (
      <View style={styles.container}>
        <View style={styles.header}>
          <TouchableOpacity onPress={() => setCurrentScreen('dashboard')} style={styles.backButton}>
            <Text style={styles.backButtonText}>← Back</Text>
          </TouchableOpacity>
          <Text style={styles.headerTitle}>Sites</Text>
        </View>
        <SitesScreen />
      </View>
    );
  }

  if (currentScreen === 'createSite') {
    return (
      <View style={styles.container}>
        <View style={styles.header}>
          <TouchableOpacity onPress={() => setCurrentScreen('sites')} style={styles.backButton}>
            <Text style={styles.backButtonText}>← Back</Text>
          </TouchableOpacity>
          <Text style={styles.headerTitle}>Create Site</Text>
        </View>
        <CreateSiteScreen />
      </View>
    );
  }

  return (
    <Container title="Owner Dashboard">
      <View style={styles.menuContainer}>
        <TouchableOpacity 
          style={styles.menuButton} 
          onPress={() => setCurrentScreen('sites')}
        >
          <Text style={styles.menuButtonText}>📋 View Sites</Text>
        </TouchableOpacity>
        
        <TouchableOpacity 
          style={styles.menuButton} 
          onPress={() => setCurrentScreen('createSite')}
        >
          <Text style={styles.menuButtonText}>➕ Create New Site</Text>
        </TouchableOpacity>
        
        <TouchableOpacity style={styles.menuButton}>
          <Text style={styles.menuButtonText}>📊 Materials (Coming Soon)</Text>
        </TouchableOpacity>
        
        <TouchableOpacity style={styles.menuButton}>
          <Text style={styles.menuButtonText}>💰 Payments (Coming Soon)</Text>
        </TouchableOpacity>
      </View>
      
      <View style={styles.logoutContainer}>
        <Button title="Logout" onPress={logout} />
      </View>
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

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: 16,
    backgroundColor: 'white',
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
  },
  backButton: {
    marginRight: 16,
  },
  backButtonText: {
    fontSize: 16,
    color: '#007AFF',
  },
  headerTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
  },
  menuContainer: {
    flex: 1,
    padding: 16,
  },
  menuButton: {
    backgroundColor: 'white',
    padding: 16,
    marginBottom: 12,
    borderRadius: 8,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  menuButtonText: {
    fontSize: 16,
    color: '#333',
    textAlign: 'center',
  },
  logoutContainer: {
    padding: 16,
    borderTopWidth: 1,
    borderTopColor: '#eee',
  },
});


