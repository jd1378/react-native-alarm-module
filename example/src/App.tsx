import {useState, useCallback} from 'react';
import {StyleSheet, View, Button, ToastAndroid} from 'react-native';
import {setAlarm, cancelAlarm} from 'react-native-alarm-module';

export default function App() {
  const [lastDate, setLastDate] = useState(new Date(Date.now() + 5 * 1000));

  const setAlarmOnPress = useCallback(() => {
    const newDate = new Date(Date.now() + 5 * 1000);
    setLastDate(newDate);
    setAlarm({
      taskName: 'ShowToastTask',
      isoDateTime: newDate.toISOString(),
      type: 'setExactAndAllowWhileIdle',
      allowedInForeground: true,
      extra: 'something extra',
    });
    ToastAndroid.show(
      `alarm set for ${newDate.toISOString()}`,
      ToastAndroid.SHORT,
    );
  }, []);

  const cancel = useCallback(() => {
    cancelAlarm({
      taskName: 'ShowToastTask',
      isoDateTime: lastDate.toISOString(),
    });
    ToastAndroid.show(
      `alarm cancelled for ${lastDate.toISOString()}`,
      ToastAndroid.SHORT,
    );
  }, [lastDate]);

  return (
    <View style={styles.container}>
      <Button onPress={setAlarmOnPress} title="Set Alarm in 5 seconds" />
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
