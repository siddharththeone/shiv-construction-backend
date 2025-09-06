import { AppRegistry, Text, View } from 'react-native';

function App() {
  return (
    <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
      <Text style={{ fontSize: 20, fontWeight: 'bold' }}>React Native is running</Text>
    </View>
  );
}

AppRegistry.registerComponent('ShivApp', () => App);


