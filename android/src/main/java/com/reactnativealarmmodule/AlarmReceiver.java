package com.reactnativealarmmodule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
  private static final String TAG = AlarmReceiver.class.getSimpleName();

  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals("launch_task")) {
      String taskName = intent.getStringExtra("task_name");
      try {
        Class<?> taskClass = Class.forName(taskName);
        Intent service = new Intent(context, taskClass);
        service.putExtras(intent);
        context.startService(service);
      } catch (ClassNotFoundException e) {
        Log.d(TAG, "Could not find task: " + e.getMessage());
      }
    }
  }
}
