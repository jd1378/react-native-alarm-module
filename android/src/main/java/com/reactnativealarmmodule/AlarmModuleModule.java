package com.reactnativealarmmodule;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.AlarmManagerCompat;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;

@ReactModule(name = AlarmModuleModule.NAME)
public class AlarmModuleModule extends ReactContextBaseJavaModule
    implements ActivityEventListener, LifecycleEventListener {

  public static final String NAME = "AlarmModule";

  private boolean handledStartIntent = false;

  public AlarmModuleModule(ReactApplicationContext reactContext) {
    super(reactContext);

    reactContext.addLifecycleEventListener(this);
  }

  @Override
  public void initialize() {
    super.initialize();
    this.getReactApplicationContext().addActivityEventListener(this);
  }

  @Override
  public void onCatalystInstanceDestroy() {
    super.onCatalystInstanceDestroy();
    this.getReactApplicationContext().removeActivityEventListener(this);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  @ReactMethod
  public void setAlarm(
      String taskName,
      String epochMilli,
      String type,
      boolean wakeup,
      boolean keepAwake,
      boolean allowedInForeground,
      String extra,
      final Promise promise) {

    try {
      if (extra == null) {
        extra = "";
      }

      long timeEpochMilli = Long.parseLong(epochMilli);
      AlarmManager alarmManager = this.getAlarmManager();

      PendingIntent pendingIntent =
        this.createPendingIntentForAlarm(
          taskName, timeEpochMilli, wakeup, keepAwake, allowedInForeground, extra);

      if (type == null) {
        type = "setAndAllowWhileIdle";
      }

      switch (type) {
        case "setAlarmClock":
          Class<?> mainActivity;
          Intent showIntent = null;

          try {
            String packageName = this.getReactApplicationContext().getPackageName();
            Intent launchIntent =
              this.getReactApplicationContext()
                .getPackageManager()
                .getLaunchIntentForPackage(packageName);
            String className = launchIntent.getComponent().getClassName();
            mainActivity = Class.forName(className);
            showIntent = new Intent(this.getReactApplicationContext(), mainActivity);
            showIntent.putExtra("extra", extra);
            showIntent.putExtra("fromAlarmModule", true);
          } catch (Exception e) {
            mainActivity = null;
            showIntent = new Intent();
          }

          int mutabilityFlag = PendingIntent.FLAG_UPDATE_CURRENT;
          if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            mutabilityFlag = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
          }
          PendingIntent pendingShowIntent =
            PendingIntent.getActivity(
              this.getReactApplicationContext(),
              this.createRequestCode(Long.toString(timeEpochMilli)),
              showIntent,
              mutabilityFlag);
          AlarmManagerCompat.setAlarmClock(
            alarmManager, timeEpochMilli, pendingShowIntent, pendingIntent);
          break;
        case "setExact":
          AlarmManagerCompat.setExact(
            alarmManager,
            wakeup ? AlarmManager.RTC_WAKEUP : AlarmManager.RTC,
            timeEpochMilli,
            pendingIntent);
        case "setExactAndAllowWhileIdle":
          AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            wakeup ? AlarmManager.RTC_WAKEUP : AlarmManager.RTC,
            timeEpochMilli,
            pendingIntent);
        default:
        case "setAndAllowWhileIdle":
          AlarmManagerCompat.setAndAllowWhileIdle(
            alarmManager,
            wakeup ? AlarmManager.RTC_WAKEUP : AlarmManager.RTC,
            timeEpochMilli,
            pendingIntent);
          break;
      }
      promise.resolve(null);
    } catch (Exception e) {
      promise.reject(e);
    }

  }

  @ReactMethod
  public void cancelAlarm(String taskName, String epochMilli, Promise promise) {
    try {
      long timeEpochMilli = Long.parseLong(epochMilli);

      this.getAlarmManager()
        .cancel(
          this.createPendingIntentForAlarm(taskName, timeEpochMilli, false, false, false, ""));
      promise.resolve(null);
    } catch (Exception e) {
      promise.reject(e);
    }
  }

  private PendingIntent createPendingIntentForAlarm(
      String taskName,
      long timeEpochMilli,
      boolean wakeup,
      boolean keepAwake,
      boolean allowedInForeground,
      String extra) {
    ReactApplicationContext context = this.getReactApplicationContext();
    String fireDate = Long.toString(timeEpochMilli);

    Intent intent = new Intent(context, AlarmReceiver.class);
    intent.setAction("launchTask");
    intent.putExtra("taskName", taskName);
    intent.putExtra("wakeup", wakeup);
    intent.putExtra("keepAwake", keepAwake);
    intent.putExtra("allowedInForeground", allowedInForeground);
    intent.putExtra("fireDate", fireDate);
    intent.putExtra("extra", extra);

    int requestCode = this.createRequestCode(fireDate);
    int mutabilityFlag = PendingIntent.FLAG_ONE_SHOT;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
      mutabilityFlag = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT;
    }
    return PendingIntent.getBroadcast(context, requestCode, intent, mutabilityFlag);
  }

  private int createRequestCode(String timeMillisStr) {
    try {
      return Integer.parseInt(timeMillisStr.substring(1, timeMillisStr.length() - 3));
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  private AlarmManager getAlarmManager() {
    return (AlarmManager) this.getReactApplicationContext().getSystemService(Context.ALARM_SERVICE);
  }

  private void handleIntent(@Nullable Intent intent) {
    if (intent != null) {
      Bundle bundle = intent.getExtras();
      if (bundle == null) {
        bundle = Bundle.EMPTY;
      }
      bundle.remove("profile"); // fix for xiaomi
      WritableMap map = Arguments.fromBundle(bundle);

      this.getReactApplicationContext()
          .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
          .emit("onNewIntent", map);
    }
  }

  @Override
  public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {}

  @Override
  public void onNewIntent(Intent intent) {
    this.handleIntent(intent);
  }

  @ReactMethod
  public void addListener(String eventName) {
    // Set up any upstream listeners or background tasks as necessary
  }

  @ReactMethod
  public void removeListeners(Integer count) {
    // Remove upstream listeners, stop unnecessary background tasks
  }

  @Override
  public void onHostResume() {
    if (this.getCurrentActivity() != null) {
      Intent intent = this.getCurrentActivity().getIntent();
      if (this.handledStartIntent) {
        // sometimes it is fired after onNewIntent, but it should not
        return;
      }
      this.handledStartIntent = true;
      if (intent.getBooleanExtra("fromAlarmModule", false)) {
        intent.putExtra("activityStarted", true);
        this.handleIntent(intent);
      }
    }
  }

  @Override
  public void onHostPause() {}

  @Override
  public void onHostDestroy() {
    this.handledStartIntent = false;
    this.getReactApplicationContext().removeLifecycleEventListener(this);
  }
}
