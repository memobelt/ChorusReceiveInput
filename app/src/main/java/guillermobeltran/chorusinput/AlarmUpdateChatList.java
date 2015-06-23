package guillermobeltran.chorusinput;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Memo on 6/18/15.
 */
public class AlarmUpdateChatList extends BroadcastReceiver {
    int _size, numNotifications = 0;
    String _task,_role;
    ChorusChat chat = new ChorusChat();
    @Override
    public void onReceive(final Context context, Intent intent) {
        chat.checkUpdate();
//        if (chat._checkUpdate){
//            Intent ntent = new Intent(context, UpdateService.class);
//            ntent.putExtra("ArrayList", intent.getExtras().getInt("ArrayList"));
//            context.startActivity(ntent);
    }
}
