package com.example.sunnysummer5.chorusreceiveinput2;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Memo on 6/18/15.
 */
public class AlarmUpdateChatList extends BroadcastReceiver {
    int _size, numNotifications = 0;
    String _task,_role;
    ChorusChat chat = new ChorusChat();
    Intent _intent;
    @Override
    public void onReceive(final Context context, Intent intent) {
        _size = intent.getExtras().getInt("ArrayList");
        _task = intent.getStringExtra("ChatNum");
        _role = intent.getStringExtra("Role");
        _intent = intent;
        String url = "http://128.237.179.10:8888/php/chatProcess.php";
        AQuery aq = new AQuery(context);
        Map<String, Object> params = chat.setUpParams(new HashMap<String, Object>(), "fetchNewChatRequester");
        params.put("role", _role);
        params.put("task", _task);
        aq.ajax(url, params, JSONArray.class, new AjaxCallback<JSONArray>() {
            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                status.getMessage();
                if (json.length()>_size) {
                    numNotifications = json.length() - _size;
                    Intent viewIntent = new Intent(context, ChorusChat.class);
                    viewIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    PendingIntent viewPendingIntent = PendingIntent.getActivity(context, 0,
                            viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Chorus").setAutoCancel(true)
                            .setWhen(System.currentTimeMillis()).setContentIntent(viewPendingIntent);
                        mBuilder.setContentText(Integer.toString(numNotifications) + " New Messages " +
                                "in chat " + _task);
                    NotificationManagerCompat nm = NotificationManagerCompat.from(context);
                    nm.notify(0, mBuilder.build());
                    _size = json.length();
                    _intent.putExtra("ArrayList",json.length());
                }
            }
        });
    }
}