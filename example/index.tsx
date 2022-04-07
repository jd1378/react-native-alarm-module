import {AppRegistry, ToastAndroid} from 'react-native';
import {subscribeToOnNewIntent} from 'react-native-alarm-module';
import {name as appName} from './app.json';
import App from './src/App';
import {showToast} from './tasks/show_toast';

AppRegistry.registerComponent(appName, () => App);

AppRegistry.registerHeadlessTask('ShowToastTask', () => showToast);

// added here because inside the react component it seems it fails to handle the event in time.
subscribeToOnNewIntent(intentArgs => {
  ToastAndroid.show(
    `onNewIntent: ${JSON.stringify(intentArgs)}`,
    ToastAndroid.SHORT,
  );
});
