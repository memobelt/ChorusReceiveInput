package com.example.sunnysummer5.chorusreceiveinput2;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class UpdateService extends Service {
    int _size, id = 001;
    String NOTIFICATION_GROUP = "notification_group";
    String _task,_role;
    public UpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
    @SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId) {
        _size = intent.getExtras().getInt("ArrayList");
//        _role = intent.getStringExtra("ChatNum");
//        _task = intent.getStringExtra("Role");
        Intent viewIntent = new Intent(getApplicationContext(), ChorusChat.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_launcher).setAutoCancel(true)
                .setContentIntent(pendingIntent).setWhen(System.currentTimeMillis())
                .setGroup(NOTIFICATION_GROUP).setContentTitle("Chorus")
                .setContentText("New Message");
        NotificationManagerCompat nmc = NotificationManagerCompat.from(getApplicationContext());
        nmc.notify(id++, notification.build());
    }
}