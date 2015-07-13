package guillermobeltran.chorusinput;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

public class ListenerServiceFromPhone extends WearableListenerService {
    String NOTIFICATION_GROUP = "notification_group";
    int id = 001;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.i("test", "onMessageReceived()");
        /*
         * Receive the message from wear
         */
        //open on phone was called from MainActivity to answer
        if(messageEvent.getPath().equals("/hello-world")) {
            Intent intent = new Intent(getApplicationContext(), ChorusChat.class);
            intent.putExtra("New Text", new String(messageEvent.getData(), StandardCharsets.UTF_8));
            Log.i("test", new String(messageEvent.getData(), StandardCharsets.UTF_8));
            intent.putExtra("caller", "ListenerServiceFromPhone");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.ic_launcher).setAutoCancel(true)
                    .setContentIntent(pendingIntent).setWhen(System.currentTimeMillis())
                    .setGroup(NOTIFICATION_GROUP).setContentTitle("Chorus")
                    .setContentText("New Message");
            NotificationManagerCompat nmc = NotificationManagerCompat.from(getApplicationContext());
            nmc.notify(id++, notification.build());

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
}