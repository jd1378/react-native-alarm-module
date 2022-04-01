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
import { setExactAndAllowWhileIdle } from 'react-native-alarm-module';

export default function App() {
  const date = Date.now() + 10 * 1000;

  const setAlarm = () => {
    setExactAndAllowWhileIdle(
      // Your task class full path
      'com.example.reactnativealarmmodule.ShowToastTask',
      // the time in iso format.
      new Date(date).toISOString(),
      // wake up device (RTC_WAKEUP)
      true,
    );
  };

  return (
    <View style={styles.container}>
      <Button onPress={setAlarm} title="Set Alarm in 10 seconds" />
    </View>
  );
}

```

## Notes

### RTC_WAKEUP

To set an alarm with `wakeup: true` you have to add the following to your `AndroidManifest.xml` file:

```xml
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

Otherwise your app will crash

### Rebooting

This library does not save and restore your scheduled alarm if the device is rebooted. you have to save them yourself before scheduling them with this library and setup them again on reboot using the `android.intent.action.BOOT_COMPLETED` broadcast and the related permission.

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
