import {ToastAndroid} from 'react-native';
import type {TaskArgs} from 'react-native-alarm-module';

export function showToast({extra}: TaskArgs): Promise<void> {
  ToastAndroid.show('task ran. extras: ' + extra, ToastAndroid.SHORT);
  return Promise.resolve();
}
