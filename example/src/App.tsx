import {useState, useCallback} from 'react';
import {StyleSheet, View, Button} from 'react-native';
import {
  setExactAndAllowWhileIdle,
  cancelAlarm,
} from 'react-native-alarm-module';

export default function App() {
  const [lastDate, setLastDate] = useState(new Date(Date.now() + 5 * 1000));

  const setAlarm = useCallback(() => {
    const newDate = new Date(Date.now() + 5 * 1000);
    setLastDate(newDate);
    setExactAndAllowWhileIdle(
      'com.example.reactnativealarmmodule.ShowToastTask',
      newDate.toISOString(),
      true,
    );
  }, []);

  const cancel = useCallback(() => {
    cancelAlarm(
      'com.example.reactnativealarmmodule.ShowToastTask',
      lastDate.toISOString(),
    );
  }, [lastDate]);

  return (
    <View style={styles.container}>
      <Button onPress={setAlarm} title="Set Alarm in 5 seconds" />
      <Button onPress={cancel} title="Cancel last alarm" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'space-around',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
