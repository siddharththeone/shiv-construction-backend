import React, { useState } from 'react';
import { View, Text, StyleSheet, TextInput, TouchableOpacity, Alert, ScrollView } from 'react-native';
import { useAuthStore } from '../store/auth';

export const CreateSiteScreen: React.FC = () => {
  const [formData, setFormData] = useState({
    name: '',
    location: '',
    address: '',
    description: '',
    budget: '',
    siteArea: '',
    buildingType: '',
    floors: '',
    startDate: '',
    expectedEndDate: ''
  });
  const [loading, setLoading] = useState(false);
  const { token } = useAuthStore();

  const API_BASE = 'https://shiv-construction-backend-production.up.railway.app/api';

  const handleInputChange = (field: string, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async () => {
    if (!formData.name.trim()) {
      Alert.alert('Error', 'Site name is required');
      return;
    }

    try {
      setLoading(true);
      
      const submitData = {
        name: formData.name.trim(),
        location: formData.location.trim() || undefined,
        address: formData.address.trim() || undefined,
        description: formData.description.trim() || undefined,
        budget: formData.budget ? parseFloat(formData.budget) : undefined,
        siteArea: formData.siteArea ? parseFloat(formData.siteArea) : undefined,
        buildingType: formData.buildingType.trim() || undefined,
        floors: formData.floors ? parseInt(formData.floors) : undefined,
        startDate: formData.startDate || undefined,
        expectedEndDate: formData.expectedEndDate || undefined
      };

      const response = await fetch(`${API_BASE}/sites`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(submitData)
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Failed to create site');
      }

      const data = await response.json();
      Alert.alert('Success', 'Site created successfully!', [
        { text: 'OK', onPress: () => {
          // Reset form
          setFormData({
            name: '',
            location: '',
            address: '',
            description: '',
            budget: '',
            siteArea: '',
            buildingType: '',
            floors: '',
            startDate: '',
            expectedEndDate: ''
          });
        }}
      ]);
    } catch (error: any) {
      console.error('Error creating site:', error);
      Alert.alert('Error', error.message || 'Failed to create site');
    } finally {
      setLoading(false);
    }
  };

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.title}>Create New Site</Text>
      
      <View style={styles.form}>
        <View style={styles.inputGroup}>
          <Text style={styles.label}>Site Name *</Text>
          <TextInput
            style={styles.input}
            value={formData.name}
            onChangeText={(value) => handleInputChange('name', value)}
            placeholder="Enter site name"
          />
        </View>

        <View style={styles.inputGroup}>
          <Text style={styles.label}>Location</Text>
          <TextInput
            style={styles.input}
            value={formData.location}
            onChangeText={(value) => handleInputChange('location', value)}
            placeholder="Enter location"
          />
        </View>

        <View style={styles.inputGroup}>
          <Text style={styles.label}>Address</Text>
          <TextInput
            style={[styles.input, styles.textArea]}
            value={formData.address}
            onChangeText={(value) => handleInputChange('address', value)}
            placeholder="Enter full address"
            multiline
            numberOfLines={3}
          />
        </View>

        <View style={styles.inputGroup}>
          <Text style={styles.label}>Description</Text>
          <TextInput
            style={[styles.input, styles.textArea]}
            value={formData.description}
            onChangeText={(value) => handleInputChange('description', value)}
            placeholder="Enter site description"
            multiline
            numberOfLines={3}
          />
        </View>

        <View style={styles.row}>
          <View style={[styles.inputGroup, styles.halfWidth]}>
            <Text style={styles.label}>Budget (₹)</Text>
            <TextInput
              style={styles.input}
              value={formData.budget}
              onChangeText={(value) => handleInputChange('budget', value)}
              placeholder="0"
              keyboardType="numeric"
            />
          </View>

          <View style={[styles.inputGroup, styles.halfWidth]}>
            <Text style={styles.label}>Site Area (sq ft)</Text>
            <TextInput
              style={styles.input}
              value={formData.siteArea}
              onChangeText={(value) => handleInputChange('siteArea', value)}
              placeholder="0"
              keyboardType="numeric"
            />
          </View>
        </View>

        <View style={styles.row}>
          <View style={[styles.inputGroup, styles.halfWidth]}>
            <Text style={styles.label}>Building Type</Text>
            <TextInput
              style={styles.input}
              value={formData.buildingType}
              onChangeText={(value) => handleInputChange('buildingType', value)}
              placeholder="e.g., Residential, Commercial"
            />
          </View>

          <View style={[styles.inputGroup, styles.halfWidth]}>
            <Text style={styles.label}>Floors</Text>
            <TextInput
              style={styles.input}
              value={formData.floors}
              onChangeText={(value) => handleInputChange('floors', value)}
              placeholder="0"
              keyboardType="numeric"
            />
          </View>
        </View>

        <View style={styles.row}>
          <View style={[styles.inputGroup, styles.halfWidth]}>
            <Text style={styles.label}>Start Date</Text>
            <TextInput
              style={styles.input}
              value={formData.startDate}
              onChangeText={(value) => handleInputChange('startDate', value)}
              placeholder="YYYY-MM-DD"
            />
          </View>

          <View style={[styles.inputGroup, styles.halfWidth]}>
            <Text style={styles.label}>Expected End Date</Text>
            <TextInput
              style={styles.input}
              value={formData.expectedEndDate}
              onChangeText={(value) => handleInputChange('expectedEndDate', value)}
              placeholder="YYYY-MM-DD"
            />
          </View>
        </View>

        <TouchableOpacity
          style={[styles.submitButton, loading && styles.submitButtonDisabled]}
          onPress={handleSubmit}
          disabled={loading}
        >
          <Text style={styles.submitButtonText}>
            {loading ? 'Creating...' : 'Create Site'}
          </Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    textAlign: 'center',
    marginVertical: 20,
    color: '#333',
  },
  form: {
    padding: 16,
  },
  inputGroup: {
    marginBottom: 16,
  },
  label: {
    fontSize: 16,
    fontWeight: '600',
    marginBottom: 8,
    color: '#333',
  },
  input: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    padding: 12,
    fontSize: 16,
    backgroundColor: 'white',
  },
  textArea: {
    height: 80,
    textAlignVertical: 'top',
  },
  row: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  halfWidth: {
    width: '48%',
  },
  submitButton: {
    backgroundColor: '#007AFF',
    padding: 16,
    borderRadius: 8,
    alignItems: 'center',
    marginTop: 20,
  },
  submitButtonDisabled: {
    backgroundColor: '#ccc',
  },
  submitButtonText: {
    color: 'white',
    fontSize: 18,
    fontWeight: 'bold',
  },
});
