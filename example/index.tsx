import {AppRegistry} from 'react-native';
import {name as appName} from './app.json';
import App from './src/App';
import {showToast} from './tasks/show_toast';

AppRegistry.registerComponent(appName, () => App);

AppRegistry.registerHeadlessTask('ShowToastTask', () => showToast);
