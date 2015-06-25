package guillermobeltran.chorusinput;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ListenerServiceFromWear extends WearableListenerService {

    private static final String ANSWER_ON_PHONE = "/answer-on-phone";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.i("test", "onMessageReceived()");
        Toast.makeText(getApplicationContext(),"onMessageReceived",Toast.LENGTH_LONG);
        /*
         * Receive the message from wear
         */
        if (messageEvent.getPath().equals(ANSWER_ON_PHONE)) {
            Intent startIntent = new Intent(this, ChorusChat.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);
        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }
}