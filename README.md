# react-native-alarm-module

A native module for scheduling alarms. the main aim is to execute a headless js task from a set alarm. this was needed for executing a time critical task.

## Installation

```sh
npm install react-native-alarm-module
```

```sh
yarn install react-native-alarm-module
```

## Usage

```js
import { setAlarm } from "react-native-alarm-module";

// ...

```

## Notes

To set an alarm with `wakeup: true` you havre to add the following to your `AndroidManifest.xml` file:

```xml
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
