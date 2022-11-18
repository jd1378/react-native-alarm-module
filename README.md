# react-native-alarm-module

A native module for scheduling alarms. the main aim is to execute a headless js task from a set alarm. this was needed for executing a time critical task.

## Supported Platforms

- Android (Min. SDK 21)

## Installation

```sh
npm install react-native-alarm-module
```

```sh
yarn add react-native-alarm-module
```

## Usage

```js
// setAlarmClock, setExact, setAndAllowWhileIdle, setExactAndAllowWhileIdle
import {setAlarm, cancelAlarm} from 'react-native-alarm-module';
import {View, Button, ToastAndroid, AppRegistry} from 'react-native';

// You have to register the task you would like executed as a headless task
AppRegistry.registerHeadlessTask('ShowToastTask', () => () => ToastAndroid.show('Alarm toast!', ToastAndroid.SHORT));

export default function App() {
  const [lastDate, setLastDate] = useState(new Date(Date.now() + 5 * 1000));

  const setAlarmOnPress = useCallback(() => {
    const newDate = new Date(Date.now() + 5 * 1000);
    setLastDate(newDate);

    setAlarm({
      taskName: 'ShowToastTask', // required
      timestamp: newDate.valueOf(), // required
      type: 'setAlarmClock', // optional
      allowedInForeground: true, // optional 
      wakeup: true, // optional
      extra: 'something extra', // optional
    });

    ToastAndroid.show(
      `alarm set for ${newDate.toISOString()}`,
      ToastAndroid.SHORT,
    );
  }, []);

  const cancel = useCallback(() => {
    cancelAlarm({
      taskName: 'ShowToastTask',
      timestamp: lastDate.valueOf(),
    });
    ToastAndroid.show(
      `alarm cancelled for ${lastDate.toISOString()}`,
      ToastAndroid.SHORT,
    );
  }, [lastDate]);

  return (
    <View>
      <Button onPress={setAlarmOnPress} title="Set Alarm in 5 seconds" />
      <Button onPress={cancel} title="Cancel last alarm" />
    </View>
  );
}

```

## Notes

### Alarm fired in background

Currently you cannot launch your app when the alarm is fired and your app is not in foreground in newer Android versions due to the newly added limitations.

### `setAlarmClock` type for android

When `setAlarmClock` type is used to set an alarm on Android, this library tries to launch your application if the clock is touched. If the needed class is not found, it simply won't do anything if the clock is touched.

When you want to handle the intent, you have to use `subscribeToOnNewIntent` method. remember to call `.remove()` when you are done with it. To handle the *launch intent* from the alarm clock icon, you have to handle the event early in your app or you will miss it.

### Canceling an alarm

`cancelAlarm` Uses your task name and the timestamp value the alarm was set to fire to cancel the alarm.
it uses the time in seconds without the first digit as the request code under the hood to create a pending intent to cancel the alarm.

### RTC_WAKEUP

To set an alarm with `wakeup: true` you have to add the following to your `AndroidManifest.xml` file, Otherwise your app will crash:

```xml
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

### Rebooting

This library does not save and restore your scheduled alarm if the device is rebooted. you have to save them yourself before scheduling them with this library and setup them again on reboot using the `android.intent.action.BOOT_COMPLETED` broadcast and the related permission.

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
