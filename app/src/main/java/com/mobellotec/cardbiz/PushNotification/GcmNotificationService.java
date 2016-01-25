package com.mobellotec.cardbiz.PushNotification;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mobellotec.cardbiz.Activity.NotificationActivity;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.Constants;


public class GcmNotificationService extends IntentService {

    public static int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    final String TAG = "GCMNotification";
    private Intent notificationIntent;

    public GcmNotificationService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {
                String message = "GCM Send error";
                String title = "Cardbiz";
                sendNotification(title, message);
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {
                String message = "GCM message deleted";
                String title = "Cardbiz";
                sendNotification(title, message);
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {
                /*for (int i = 0; i < 3; i++) {
                    Log.i(TAG,
                            "Working..." + (i + 1) + "5/@"
                                    + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.i(TAG, "Completed Work..." + SystemClock.elapsedRealtime());*/
                String message = extras.containsKey(GCMConfig.MESSAGE_KEY) ? extras.get(GCMConfig.MESSAGE_KEY).toString() : "";
                String title = extras.containsKey(GCMConfig.TITLE_KEY) ? extras.get(GCMConfig.TITLE_KEY).toString() : "CardBiz";
                if (!message.equalsIgnoreCase("")) {
//                    sendNotification(title, message);
                    receiveNotification(message);
                }
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    /**
     * send notification message to intent.
     *
     * @param message notification message.
     */
    private void sendNotification(String title, String message) {

        notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        notificationIntent = new Intent(this, NotificationActivity.class);
        notificationIntent.putExtra(Constants.BUMP_RESULT, message);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotificationActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent contentIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message).setAutoCancel(true)
                .setContentIntent(contentIntent);
        notificationManager.notify(NOTIFICATION_ID++, builder.build());
    }

    private void receiveNotification(String message){
        notificationIntent = new Intent(this, NotificationActivity.class);
        notificationIntent.putExtra(Constants.BUMP_RESULT, message);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(notificationIntent);
    }
}
