import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, FlatList, TouchableOpacity, Alert } from 'react-native';
import { useAuthStore } from '../store/auth';

interface Site {
  _id: string;
  name: string;
  location?: string;
  status: string;
  progressPercent: number;
  description?: string;
  contractors: any[];
  suppliers: any[];
}

export const SitesScreen: React.FC = () => {
  const [sites, setSites] = useState<Site[]>([]);
  const [loading, setLoading] = useState(true);
  const { token, user } = useAuthStore();

  const API_BASE = 'https://shiv-construction-backend-production.up.railway.app/api';

  const fetchSites = async () => {
    try {
      setLoading(true);
      const response = await fetch(`${API_BASE}/sites`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        throw new Error('Failed to fetch sites');
      }

      const data = await response.json();
      setSites(data.sites || []);
    } catch (error) {
      console.error('Error fetching sites:', error);
      Alert.alert('Error', 'Failed to load sites');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (token) {
      fetchSites();
    }
  }, [token]);

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'NOT_STARTED': return '#gray';
      case 'IN_PROGRESS': return '#blue';
      case 'COMPLETED': return '#green';
      case 'ON_HOLD': return '#orange';
      default: return '#gray';
    }
  };

  const renderSite = ({ item }: { item: Site }) => (
    <TouchableOpacity style={styles.siteCard}>
      <Text style={styles.siteName}>{item.name}</Text>
      {item.location && <Text style={styles.siteLocation}>📍 {item.location}</Text>}
      <View style={styles.statusRow}>
        <Text style={[styles.status, { color: getStatusColor(item.status) }]}>
          {item.status.replace('_', ' ')}
        </Text>
        <Text style={styles.progress}>{item.progressPercent}%</Text>
      </View>
      {item.description && <Text style={styles.description}>{item.description}</Text>}
      <View style={styles.assignedRow}>
        <Text style={styles.assignedText}>
          Contractors: {item.contractors.length} | Suppliers: {item.suppliers.length}
        </Text>
      </View>
    </TouchableOpacity>
  );

  if (loading) {
    return (
      <View style={styles.container}>
        <Text style={styles.loadingText}>Loading sites...</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <Text style={styles.title}>
        {user?.role === 'OWNER' ? 'All Sites' : 'My Assigned Sites'}
      </Text>
      
      {sites.length === 0 ? (
        <View style={styles.emptyContainer}>
          <Text style={styles.emptyText}>No sites found</Text>
          {user?.role === 'OWNER' && (
            <Text style={styles.emptySubtext}>Create your first site to get started</Text>
          )}
        </View>
      ) : (
        <FlatList
          data={sites}
          renderItem={renderSite}
          keyExtractor={(item) => item._id}
          style={styles.list}
          showsVerticalScrollIndicator={false}
        />
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
    backgroundColor: '#f5f5f5',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 16,
    color: '#333',
  },
  loadingText: {
    textAlign: 'center',
    marginTop: 50,
    fontSize: 16,
    color: '#666',
  },
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  emptyText: {
    fontSize: 18,
    color: '#666',
    marginBottom: 8,
  },
  emptySubtext: {
    fontSize: 14,
    color: '#999',
  },
  list: {
    flex: 1,
  },
  siteCard: {
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
  siteName: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 4,
  },
  siteLocation: {
    fontSize: 14,
    color: '#666',
    marginBottom: 8,
  },
  statusRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  status: {
    fontSize: 14,
    fontWeight: '600',
    textTransform: 'capitalize',
  },
  progress: {
    fontSize: 14,
    color: '#666',
  },
  description: {
    fontSize: 14,
    color: '#666',
    marginBottom: 8,
  },
  assignedRow: {
    borderTopWidth: 1,
    borderTopColor: '#eee',
    paddingTop: 8,
  },
  assignedText: {
    fontSize: 12,
    color: '#999',
  },
});
