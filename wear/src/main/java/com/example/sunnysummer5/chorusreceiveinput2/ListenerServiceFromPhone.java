package com.example.sunnysummer5.chorusreceiveinput2;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ListenerServiceFromPhone extends WearableListenerService {
    private final static String HELLO_WORLD = "/hello-world";
    ChorusChat c = new ChorusChat();

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.i("test", "onMessageReceived()");
        /*
         * Receive the message from wear
         */
        //open on phone was called from MainActivity to answer
        if (messageEvent.getPath().equals(HELLO_WORLD)) {
            Intent intent = new Intent(getApplicationContext(), ChorusChat.class);
            final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.ic_launcher).setAutoCancel(true)
                    .setContentIntent(pendingIntent).setWhen(System.currentTimeMillis())
                    .setGroup(c.NOTIFICATION_GROUP).setContentTitle("Chorus")
                    .setContentText("New Message");
            NotificationManagerCompat nmc = NotificationManagerCompat.from(getApplicationContext());
            nmc.notify(c.id++, notification.build());

            c.chatText.setText(messageEvent.getData().toString());
        }
        else {
            super.onMessageReceived(messageEvent);
            Log.i("test", "Message path does not match");
        }
    }
}