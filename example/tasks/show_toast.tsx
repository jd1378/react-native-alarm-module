import {ToastAndroid} from 'react-native';

export function showToast(): Promise<void> {
  ToastAndroid.show('This is called from an alarm', ToastAndroid.SHORT);
  return Promise.resolve();
}
