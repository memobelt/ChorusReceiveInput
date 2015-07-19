package guillermobeltran.chorusinput;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ListenerServiceFromPhone extends WearableListenerService {
    String NOTIFICATION_GROUP = "notification_group";
    int id = 001;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        /*
         * Receive the message from wear
         */
        //open on phone was called from MainActivity to answer
        if(messageEvent.getPath().equals("/hello-world")) {
            Intent intent = new Intent(getApplicationContext(), ChorusChat.class);
            String temp_message = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            int separate = temp_message.indexOf("|");
            intent.putExtra("Role", temp_message.substring(0, separate));
            intent.putExtra("New Text", temp_message.substring(separate+1, temp_message.length() - 1));
            intent.putExtra("ChatNum", temp_message.substring(temp_message.length() - 1));
            intent.putExtra("caller", "ListenerServiceFromPhone");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            /*final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.ic_launcher).setAutoCancel(true)
                    .setContentIntent(pendingIntent).setWhen(System.currentTimeMillis())
                    .setGroup(NOTIFICATION_GROUP).setContentTitle("Chorus")
                    .setContentText(temp_message.substring(0, temp_message.length()-1));
            NotificationManagerCompat nmc = NotificationManagerCompat.from(getApplicationContext());
            nmc.notify(id++, notification.build()); */

            startActivity(intent);
        }

        else {
            super.onMessageReceived(messageEvent);
            Log.i("test", "Message path does not match");
        }
    }
    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);
        Log.i("test",peer.getDisplayName());
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        super.onPeerDisconnected(peer);
        Log.i("test",peer.getDisplayName());
    }
    public boolean isForeground(String myPackage) {
        boolean foreground = false;
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        /*if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList =
                    manager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcessInfoList) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.getClass().getSimpleName().contains("ChorusChat")) {
                            foreground = true;
                        }
                        /*if (activeProcess.equals(getApplicationContext().getPackageName())) {
                            foreground = true;
                        }*/
                    /*}
                }
            }
        }
        else {
            List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
            ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
            if(componentInfo.getClassName().contains("ChorusChat")) {
                foreground = true;
            }
            /*if(componentInfo.getPackageName().equals(myPackage)) {
                foreground = true;
            }*/
        //}
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        if(componentInfo.getClassName().contains("ChorusChat")) {
            Log.i("test", "foreground");
            foreground = true;
        }
            /*if(componentInfo.getPackageName().equals(myPackage)) {
                foreground = true;
            }*/
        if(!foreground) {
            Log.i("test", "background");
        }
        return foreground;
    }
}