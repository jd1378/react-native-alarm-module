package com.reactnativealarmmodule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.AlarmManagerCompat;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import java.time.OffsetDateTime;

@ReactModule(name = AlarmModuleModule.NAME)
public class AlarmModuleModule extends ReactContextBaseJavaModule {

  public static final String NAME = "AlarmModule";

  public AlarmModuleModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  @ReactMethod
  public void setAlarm(
      String taskName,
      String isoDateTime,
      String type,
      boolean wakeup,
      boolean keepAwake,
      boolean allowedInForeground,
      String extra) {

    if (extra == null) {
      extra = "";
    }

    long timeEpochMilli = OffsetDateTime.parse(isoDateTime).toInstant().toEpochMilli();
    AlarmManager alarmManager = this.getAlarmManager();

    PendingIntent pendingIntent =
        this.createPendingIntentForAlarm(
            taskName, timeEpochMilli, wakeup, keepAwake, allowedInForeground, extra);

    if (type == null) {
      type = "setAndAllowWhileIdle";
    }

    switch (type) {
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
  }

  @ReactMethod
  public void cancelAlarm(String taskName, String isoDateTime) {
    long timeEpochMilli = OffsetDateTime.parse(isoDateTime).toInstant().toEpochMilli();
    String fireDate = Long.toString(timeEpochMilli);
    int requestCode = this.createRequestCode(fireDate);
    Intent intent = new Intent(this.getReactApplicationContext(), AlarmReceiver.class);

    this.getAlarmManager()
        .cancel(
            this.createPendingIntentForAlarm(taskName, timeEpochMilli, false, false, false, ""));
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
}
