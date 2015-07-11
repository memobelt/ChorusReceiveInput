package guillermobeltran.chorusinput;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ListenerServiceFromWear extends WearableListenerService {
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.i("test", "onMessageReceived()");
        /*
         * Receive the message from wear
         */
        //open on phone was called from MainActivity to answer
        if (messageEvent.getPath().equals("/main-activity-on-phone")) {
            Intent startIntent = new Intent(this, AvailableChats.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);
        }
        //open on phone was called from microphone to take a picture
        else if (messageEvent.getPath().equals("/microphone-on-phone")) {
            Intent startIntent = new Intent(this, TakePicture.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);
        }
        //open on phone was called from microphone or from generated responses. need to put new text into chat
        else if (messageEvent.getPath().equals("/speech-on-phone") ||
                messageEvent.getPath().equals("/response")) {
            Intent startIntent = new Intent(this, ChorusChat.class);
            startIntent.putExtra("Speech", true);
            startIntent.putExtra("Asking", false);
            startIntent.putExtra("Input", messageEvent.getData().toString());
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);
        }
        else {
            super.onMessageReceived(messageEvent);
            Log.i("test", "Message path does not match");
        }
    }
}