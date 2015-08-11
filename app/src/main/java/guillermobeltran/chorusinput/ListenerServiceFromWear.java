package guillermobeltran.chorusinput;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import guillermobeltran.chorusinput.UserManagement.LoginActivity;

/*
Created by Summer Kitahara
This service receives messages from the watch.
 */
public class ListenerServiceFromWear extends WearableListenerService {
    String NOTIFICATION_GROUP = "notification_group";
    int id = 001;
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        /*
         * Receive the message from wear
         */
        //open on phone was called from MainActivity to answer
        if (messageEvent.getPath().equals("/main-activity-on-phone")) {
            Intent startIntent = new Intent(this, ChorusChat.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startIntent.putExtra("ChatNum", new String(messageEvent.getData(), StandardCharsets.UTF_8));
            startIntent.putExtra("Asking", false);
            startIntent.putExtra("Speech", false);
            startIntent.putExtra("Yelp", false);
            startIntent.putExtra("Answer", true);
            startIntent.putExtra("Role", "crowd");
            startIntent.putExtra("Foreground", activityRunning(getApplicationContext(), "ChorusChat"));
            startActivity(startIntent);

        }
        //open on phone was called from microphone to take a picture
        else if (messageEvent.getPath().equals("/microphone-on-phone")) {
            Intent startIntent = new Intent(this, TakePicture.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(startIntent);
        }
        else if (messageEvent.getPath().equals("/login-on-phone")) {
            Intent startIntent = new Intent(this, LoginActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(startIntent);
        }
        //open on phone was called from microphone or from generated responses. need to put new text into chat
        else if (messageEvent.getPath().equals("/speech-on-phone")) {
            String temp_message = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Intent startIntent = new Intent(this, ChorusChat.class);
            startIntent.putExtra("Speech", true);
            startIntent.putExtra("Asking", false);
            startIntent.putExtra("Update", false);
            startIntent.putExtra("Yelp", false);

            int role_message = temp_message.indexOf("%#");
            int message_time = temp_message.indexOf("~");
            int time_chatNum = temp_message.indexOf("|");
            startIntent.putExtra("Role", temp_message.substring(0, role_message));
            startIntent.putExtra("Input", temp_message.substring(role_message + 2, message_time));
            startIntent.putExtra("Time", temp_message.substring(message_time+1, time_chatNum));
            startIntent.putExtra("ChatNum", temp_message.substring(time_chatNum+1));
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(startIntent);
        }
        else {
            super.onMessageReceived(messageEvent);
            Log.i("test", "Message path does not match");
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

    //see if ChorusChat activity is running or not to determine if ChorusChat should open after
    //sending chatlineinfo to watch
    public boolean activityRunning(Context context, String class_name) {
        boolean inForeground = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList = activityManager.getRunningTasks(1);
        ComponentName componentName = runningTaskInfoList.get(0).topActivity;
        if (componentName.getClassName().toLowerCase().contains(class_name.toLowerCase())) {
            inForeground = true;
        }
        return inForeground;
    }
}