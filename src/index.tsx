import {NativeModules, Platform} from 'react-native';
import {required} from '@/utils';

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

interface AlarmModuleInterface {
  setAlarm(
    taskName: string,
    isoDateTime: string,
    type: string,
    wakeup: boolean,
    keepAwake: boolean,
    allowedInForeground: boolean,
    extra: string,
  ): void;
  cancelAlarm(taskName: string, isoDateTime: string): void;
}

export type AlarmOptions = {
  /** Task name that is registered using AppRegistry. */
  taskName: string;
  /** ISO 8601 formatted date time string. js provides `.toISOString()` on `Date` instance.  */
  isoDateTime: string;
  /** **ANDROID:** the type of alarm. Defaults to `'setAndAllowWhileIdle'`  */
  type?: 'setExact' | 'setExactAndAllowWhileIdle' | 'setAndAllowWhileIdle';
  /** **ANDROID:** Should this alarm wake up device ? Uses `RTC_WAKEUP` if true, `RTC` if false */
  wakeup?: boolean;
  /** **ANDROID:** should this task acquire wake wock ? */
  keepAwake?: boolean;
  /** **ANDROID:** should this task be allowed to run in foreground ? */
  allowedInForeground?: boolean;
  /** Extra string to pass to task as `extra` prop. (can be used for passing stringified json) */
  extra?: string;
};

export function setAlarm(options: AlarmOptions): void {
  if (!options) {
    throw new Error('AlarmOptions is required.');
  }
  required(options, 'taskName');
  required(options, 'isoDateTime');
  const {
    taskName,
    isoDateTime,
    type = 'setAndAllowWhileIdle',
    wakeup = false,
    keepAwake = false,
    allowedInForeground = false,
    extra = '',
  } = options;

  AlarmModule.setAlarm(
    taskName,
    isoDateTime,
    type,
    wakeup,
    keepAwake,
    allowedInForeground,
    extra,
  );
}

export function cancelAlarm(
  options: Pick<AlarmOptions, 'taskName' | 'isoDateTime'>,
): void {
  if (!options) {
    throw new Error('AlarmOptions is required.');
  }
  required(options, 'taskName');
  required(options, 'isoDateTime');
  const {taskName, isoDateTime} = options;

  AlarmModule.cancelAlarm(taskName, isoDateTime);
}

export type TaskArgs = {
  taskName: string;
  wakeup: boolean;
  keepAwake: boolean;
  fireDate: string;
  extra: string;
};

export default AlarmModule;
