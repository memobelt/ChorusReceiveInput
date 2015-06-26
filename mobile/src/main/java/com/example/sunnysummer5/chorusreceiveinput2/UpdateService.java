package com.example.sunnysummer5.chorusreceiveinput2;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class UpdateService extends Service {
    int _size, numNotifications = 0;
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
                        numNotifications++;
                        Intent viewIntent = new Intent(getApplicationContext(), ChorusChat.class);
                        viewIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        PendingIntent viewPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                                viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Chorus").setAutoCancel(true)
                                .setWhen(System.currentTimeMillis()).setContentIntent(viewPendingIntent);
                        if (numNotifications == 1) {
                            mBuilder.setContentText("1 New Message");
                        } else {
                            mBuilder.setContentText(Integer.toString(numNotifications) + " New Messages");
                        }
                        NotificationManagerCompat nm = NotificationManagerCompat.from(getApplicationContext());
                        nm.notify(0, mBuilder.build());
    }
}
