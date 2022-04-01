import {NativeModules, Platform} from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-alarm-module' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ios: "- You have run 'pod install'\n", default: ''}) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const AlarmModule = (
  NativeModules.AlarmModule
    ? NativeModules.AlarmModule
    : new Proxy(
        {},
        {
          get() {
            throw new Error(LINKING_ERROR);
          },
        },
      )
) as AlarmModuleInterface;
export interface AlarmModuleInterface {
  setAlarmClock(taskName: string, isoDateTime: string): void;
  setAndAllowWhileIdle(
    taskName: string,
    isoDateTime: string,
    wakeup: boolean,
  ): void;
  setExactAndAllowWhileIdle(
    taskName: string,
    isoDateTime: string,
    wakeup: boolean,
  ): void;
  setExact(taskName: string, isoDateTime: string, wakeup: boolean): void;
}

export const {
  setAlarmClock,
  setAndAllowWhileIdle,
  setExact,
  setExactAndAllowWhileIdle,
} = AlarmModule;

export default AlarmModule;
