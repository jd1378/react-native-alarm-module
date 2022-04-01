import {StyleSheet, View, Button} from 'react-native';
import {setExactAndAllowWhileIdle} from 'react-native-alarm-module';

export default function App() {
  const date = Date.now() + 10 * 1000;

  const setAlarm = () => {
    setExactAndAllowWhileIdle(
      'com.example.reactnativealarmmodule.ShowToastTask',
      new Date(date).toISOString(),
      true,
    );
  };

  return (
    <View style={styles.container}>
      <Button onPress={setAlarm} title="SetAlarm in 10 seconds" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
