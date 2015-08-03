package guillermobeltran.chorusinput;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ListenerServiceFromPhone extends WearableListenerService {
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        /*
         * Receive the message from wear
         */
        //open on phone was called from MainActivity to answer
        if (messageEvent.getPath().equals("/hello-world")) {
            //parse message
            String temp_message = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            int role_message = temp_message.indexOf("|");
            int message_time = temp_message.indexOf("+=+");
            int time_chatNum = temp_message.indexOf("~");
            int chatNum_ID = temp_message.indexOf("#|#");

            Intent intent = new Intent(getApplicationContext(), ChorusChat.class);
            //Answer question Chorus Chat
            if ((temp_message.substring(0, role_message)).startsWith("?")) {
                intent.putExtra("Role", temp_message.substring(1, role_message));
                intent.putExtra("Foreground", true);
            }
            //Post to Chorus Chat
            else {
                intent.putExtra("Role", temp_message.substring(0, role_message));
                intent.putExtra("Foreground", appInForeground(getApplicationContext()));
            }
            intent.putExtra("New Text", temp_message.substring(role_message + 1, message_time));
            intent.putExtra("Time", temp_message.substring(message_time + 3, time_chatNum));
            intent.putExtra("ChatNum", temp_message.substring(time_chatNum + 1, chatNum_ID));
            intent.putExtra("ID", temp_message.substring(chatNum_ID + 3));
            intent.putExtra("caller", "ListenerServiceFromPhone");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        //open notification on watch
        else if (messageEvent.getPath().equals("/hello-world-open")) {
            Intent intent = new Intent(getApplicationContext(), ChorusChat.class);
            intent.putExtra("Foreground", appInForeground(getApplicationContext()));
            intent.putExtra("caller", "Open");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            super.onMessageReceived(messageEvent);
            Log.e(ListenerServiceFromPhone.class.getSimpleName(), "Message path does not match");
        }
    }

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        super.onPeerDisconnected(peer);
    }

    //see if ChorusChat activity is running or not to determine if ChorusChat should open after updating message
    public boolean appInForeground(Context context) {
        boolean inForeground = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList = activityManager.getRunningTasks(1);
        ComponentName componentName = runningTaskInfoList.get(0).topActivity;
        if (componentName.getClassName().contains("ChorusChat")) {
            inForeground = true;
        }
        return inForeground;
    }
    /*public boolean appInForeground(Context context) {
        boolean inForeground = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList =
                    activityManager.getRunningAppProcesses();

            for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcessInfoList) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    if(processInfo.processName.contains("ChorusChat")) {
                        inForeground = true;
                    }
                    /*for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            inForeground = true;
                        }
                    }*/
                /*}
            }
        } else {
            List<ActivityManager.RunningTaskInfo> runningTaskInfoList = activityManager.getRunningTasks(1);
            ComponentName componentName = runningTaskInfoList.get(0).topActivity;
            /*if (componentName.getPackageName().equals(context.getPackageName())) {
                inBackground = false;
            }*/
            /*if(componentName.getClassName().contains("ChorusChat")) {
                inForeground = true;
            }
        }
        return inForeground;
    }*/
}