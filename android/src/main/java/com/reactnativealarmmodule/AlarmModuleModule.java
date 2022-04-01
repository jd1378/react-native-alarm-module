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
  public void setAlarmClock(String taskName, String isoDateTime) {
    long timeEpochMilli = OffsetDateTime.parse(isoDateTime).toInstant().toEpochMilli();
    AlarmManagerCompat.setAlarmClock(
        this.getAlarmManager(),
        timeEpochMilli,
        null,
        this.createPendingIntentForAlarm(taskName, false, timeEpochMilli));
  }

  @ReactMethod
  public void setAndAllowWhileIdle(String taskName, String isoDateTime, boolean wakeup) {
    long timeEpochMilli = OffsetDateTime.parse(isoDateTime).toInstant().toEpochMilli();
    AlarmManagerCompat.setAndAllowWhileIdle(
        this.getAlarmManager(),
        wakeup ? AlarmManager.RTC_WAKEUP : AlarmManager.RTC,
        timeEpochMilli,
        this.createPendingIntentForAlarm(taskName, wakeup, timeEpochMilli));
  }

  @ReactMethod
  public void setExactAndAllowWhileIdle(String taskName, String isoDateTime, boolean wakeup) {
    long timeEpochMilli = OffsetDateTime.parse(isoDateTime).toInstant().toEpochMilli();
    AlarmManagerCompat.setExactAndAllowWhileIdle(
        this.getAlarmManager(),
        wakeup ? AlarmManager.RTC_WAKEUP : AlarmManager.RTC,
        timeEpochMilli,
        this.createPendingIntentForAlarm(taskName, wakeup, timeEpochMilli));
  }

  @ReactMethod
  public void setExact(String taskName, String isoDateTime, boolean wakeup) {
    long timeEpochMilli = OffsetDateTime.parse(isoDateTime).toInstant().toEpochMilli();
    AlarmManagerCompat.setExact(
        this.getAlarmManager(),
        wakeup ? AlarmManager.RTC_WAKEUP : AlarmManager.RTC,
        timeEpochMilli,
        this.createPendingIntentForAlarm(taskName, wakeup, timeEpochMilli));
  }

  @ReactMethod
  public void cancelAlarm(String taskName, String isoDateTime) {
    long timeEpochMilli = OffsetDateTime.parse(isoDateTime).toInstant().toEpochMilli();
    PendingIntent pi = this.createPendingIntentForAlarm(taskName, false, timeEpochMilli);
    this.getAlarmManager().cancel(pi);
  }

  private PendingIntent createPendingIntentForAlarm(
      String taskName, boolean wakeup, long timeEpochMilli) {
    ReactApplicationContext context = this.getReactApplicationContext();
    String fire_date = Long.toString(timeEpochMilli);

    Intent intent = new Intent(context, AlarmReceiver.class);
    intent.setAction("launch_task");
    intent.putExtra("task_name", taskName);
    intent.putExtra("wakeup", wakeup);
    intent.putExtra("fire_date", fire_date);

    int requestCode = Integer.parseInt(fire_date.substring(1, fire_date.length() - 3));

    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);

    return pendingIntent;
  }

  private AlarmManager getAlarmManager() {
    return (AlarmManager) this.getReactApplicationContext().getSystemService(Context.ALARM_SERVICE);
  }
}
