package com.reactnativealarmmodule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.facebook.react.HeadlessJsTaskService;

public class AlarmReceiver extends BroadcastReceiver {
  private static final String TAG = AlarmReceiver.class.getSimpleName();

  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals("launchTask")) {

      String taskName = intent.getStringExtra("taskName");
      boolean keepAwake = intent.getBooleanExtra("keepAwake", false);

      Intent serviceIntent = new Intent(context, UniversalService.class);
      serviceIntent.putExtras(intent);
      context.startService(serviceIntent);
      if (keepAwake) {
        HeadlessJsTaskService.acquireWakeLockNow(context);
      }
    }
  }
}
