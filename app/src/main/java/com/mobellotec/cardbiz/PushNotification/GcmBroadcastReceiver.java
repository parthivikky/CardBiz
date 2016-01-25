package com.mobellotec.cardbiz.PushNotification;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;


public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ComponentName componentName = new ComponentName(
				context.getPackageName(),
				GcmNotificationService.class.getName());
		startWakefulService(context, (intent.setComponent(componentName)));
		setResultCode(Activity.RESULT_OK);
	}
}
