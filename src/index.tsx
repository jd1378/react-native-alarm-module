import {NativeEventEmitter, NativeModules, Platform} from 'react-native';
import {required} from './utils';

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
    timestamp: string,
    type: string,
    wakeup: boolean,
    keepAwake: boolean,
    allowedInForeground: boolean,
    extra: string,
  ): Promise<void>;
  cancelAlarm(taskName: string, timestamp: string): Promise<void>;
}

export type AlarmOptions = {
  /** Task name that is registered using AppRegistry. */
  taskName: string;
  /** the number of milliseconds since the epoch of 1970-01-01T00:00:00Z */
  timestamp: number;
  /** **ANDROID:** the type of alarm. Defaults to `'setAlarmClock'`  */
  type?:
    | 'setAlarmClock'
    | 'setExact'
    | 'setExactAndAllowWhileIdle'
    | 'setAndAllowWhileIdle';
  /** **ANDROID:** Should this alarm wake up device ? Uses `RTC_WAKEUP` if true, `RTC` if false */
  wakeup?: boolean;
  /** **ANDROID:** should this task acquire wake wock ? */
  keepAwake?: boolean;
  /** **ANDROID:** should this task be allowed to run in foreground ? */
  allowedInForeground?: boolean;
  /** Extra string to pass to task as `extra` prop. (can be used for passing stringified json) */
  extra?: string;
};

export async function setAlarm(options: AlarmOptions): Promise<void> {
  if (!options) {
    throw new Error('AlarmOptions is required.');
  }
  required(options, 'taskName');
  required(options, 'timestamp');
  const {
    taskName,
    timestamp,
    type = 'setAlarmClock',
    wakeup = false,
    keepAwake = false,
    allowedInForeground = false,
    extra = '',
  } = options;

  await AlarmModule.setAlarm(
    taskName,
    timestamp.toString(),
    type,
    wakeup,
    keepAwake,
    allowedInForeground,
    extra,
  );
}

export async function cancelAlarm(
  options: Pick<AlarmOptions, 'taskName' | 'timestamp'>,
): Promise<void> {
  if (!options) {
    throw new Error('AlarmOptions is required.');
  }
  required(options, 'taskName');
  required(options, 'timestamp');
  const {taskName, timestamp} = options;
  await AlarmModule.cancelAlarm(taskName, timestamp.toString());
}

export type TaskArgs = {
  taskName: string;
  wakeup: boolean;
  keepAwake: boolean;
  fireDate: string;
  extra: string;
};

const alarmModuleEmitter = new NativeEventEmitter(AlarmModule as any);

/**
 * adds the handler to call when user touches alarm clock icon in status bar.
 * **ANDROID only**.
 * Don't forget to call `.remove()` on returned subscription on `componentWillUnmount` or related functions to prevent leak.
 *
 * Calls your handler with intent's arguments
 *  */
export function subscribeToOnNewIntent(
  handler: (intentArgs: Record<string, unknown> | null) => void,
) {
  return alarmModuleEmitter.addListener('onNewIntent', handler);
}

export default {
  setAlarm,
  cancelAlarm,
  NativeModule: AlarmModule,
};
