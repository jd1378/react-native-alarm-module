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
import { setExactAndAllowWhileIdle, cancelAlarm } from 'react-native-alarm-module';

export default function App() {
  const [lastDate, setLastDate] = useState(new Date(Date.now() + 10 * 1000));

  const setAlarm = useCallback(() => {
    const newDate = new Date(Date.now() + 10 * 1000);
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
      <Button onPress={setAlarm} title="SetAlarm in 10 seconds" />
      <Button onPress={cancel} title="Cancel last alarm" />
    </View>
  );
}

```

## Notes

### Canceling an alarm

`cancelAlarm` Uses your task class path and the ISO time the alarm was set to fire to cancel the alarm.
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
