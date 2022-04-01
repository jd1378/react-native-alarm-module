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

    AlarmManagerCompat.setAlarmClock(
        this.getAlarmManager(),
        OffsetDateTime.parse(isoDateTime).toInstant().toEpochMilli(),
        null,
        this.createPendingIntentForAlarm(taskName, false));
  }

  @ReactMethod
  public void setAndAllowWhileIdle(String taskName, String isoDateTime, boolean wakeup) {

    AlarmManagerCompat.setAndAllowWhileIdle(
        this.getAlarmManager(),
        wakeup ? AlarmManager.RTC_WAKEUP : AlarmManager.RTC,
        OffsetDateTime.parse(isoDateTime).toInstant().toEpochMilli(),
        this.createPendingIntentForAlarm(taskName, wakeup));
  }

  @ReactMethod
  public void setExactAndAllowWhileIdle(String taskName, String isoDateTime, boolean wakeup) {

    AlarmManagerCompat.setExactAndAllowWhileIdle(
        this.getAlarmManager(),
        wakeup ? AlarmManager.RTC_WAKEUP : AlarmManager.RTC,
        OffsetDateTime.parse(isoDateTime).toInstant().toEpochMilli(),
        this.createPendingIntentForAlarm(taskName, wakeup));
  }

  @ReactMethod
  public void setExact(String taskName, String isoDateTime, boolean wakeup) {

    AlarmManagerCompat.setExact(
        this.getAlarmManager(),
        wakeup ? AlarmManager.RTC_WAKEUP : AlarmManager.RTC,
        OffsetDateTime.parse(isoDateTime).toInstant().toEpochMilli(),
        this.createPendingIntentForAlarm(taskName, wakeup));
  }

  private PendingIntent createPendingIntentForAlarm(String taskName, boolean wakeup) {
    ReactApplicationContext context = this.getReactApplicationContext();

    Intent intent = new Intent(context, AlarmReceiver.class);
    intent.setAction("launch_task");
    intent.putExtra("task_name", taskName);
    intent.putExtra("wakeup", wakeup);

    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

    return pendingIntent;
  }

  private AlarmManager getAlarmManager() {
    return (AlarmManager) this.getReactApplicationContext().getSystemService(Context.ALARM_SERVICE);
  }
}
