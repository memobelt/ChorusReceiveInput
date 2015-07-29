package guillermobeltran.chorusinput;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
/*
Created by Summer.
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
            //startIntent.putExtra("caller", "Listener");
            startActivity(startIntent);

        }
        //open on phone was called from microphone to take a picture
        else if (messageEvent.getPath().equals("/microphone-on-phone")) {
            Intent startIntent = new Intent(this, TakePicture.class);
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

            int message_time = temp_message.indexOf("~");
            int time_chatNum = temp_message.indexOf("|");
            startIntent.putExtra("ChatNum", temp_message.substring(time_chatNum+1));
            startIntent.putExtra("Role", "requester");
            startIntent.putExtra("Input", temp_message.substring(0, message_time));
            startIntent.putExtra("Time", temp_message.substring(message_time+1, time_chatNum));

            //Intent viewIntent = new Intent(getApplicationContext(), ChorusChat.class);
            //viewIntent.putExtra("ChatNum", temp_message.substring(temp_message.length()-1));
            //viewIntent.putExtra("Role", "requester");

            /*PendingIntent viewPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                    startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Chorus").setAutoCancel(true)
                    .setWhen(System.currentTimeMillis()).setContentIntent(viewPendingIntent)
                    .setGroup(NOTIFICATION_GROUP);
            //mBuilder.setContentText(Integer.toString(numNotifications) + " New Messages " +
            //        "in Chat " + _task);
            mBuilder.setContentText(temp_message.substring(0, temp_message.length()-1));
            NotificationManager nmgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nmgr.notify(id++, mBuilder.build());*/

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
}