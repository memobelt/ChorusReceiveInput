package com.example.sunnysummer5.chorusreceiveinput2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ChorusChat extends Activity {
    String _task, _role;
    ListView _chatList;
    Button reply;
    TextView mTextView, chatText;
    ArrayList<ChatLineInfo> _chatLineInfoArrayList;
    ArrayList<String> _arrayList = new ArrayList<String>();
    ChatLineInfo _cli = new ChatLineInfo();
    ArrayAdapter _adapter;
    Boolean _canUpdate, _checkUpdate;
    int _size;
    AlarmUpdateChatList1 _receiver = new AlarmUpdateChatList1();
    String NOTIFICATION_GROUP = "notification_group";
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initializations
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chorus_chat);
        _canUpdate = true;
        _chatLineInfoArrayList = new ArrayList<ChatLineInfo>();
        id = 001;

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                chatText = (TextView) findViewById(R.id.TextArea);
                //_chatList = (ListView) findViewById(R.id.ChatList);
                reply = (Button) findViewById(R.id.replyButton);
                reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { //reply goes back to Microphone and Camera activity
                        Intent reply_intent = new Intent(getApplicationContext(), Microphone.class);
                        startActivity(reply_intent);
                    }
                });

                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {

                    //get all the intents
                    if (getIntent().getExtras().getBoolean("Asking")) {
                        String words = getIntent().getStringExtra("Words");
                        postData(words);
                    }
                    _task = getIntent().getStringExtra("ChatNum");
                    _role = getIntent().getStringExtra("Role");

                    //make sure the text is white
                    _adapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_list_item_1, _arrayList) {
                        @Override
                        public View getView(int position, View convertView,
                                            ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);

                            TextView textView = (TextView) view.findViewById(android.R.id.text1);

                            /*YOUR CHOICE OF COLOR*/
                            textView.setTextColor(Color.WHITE);

                            return view;
                        }
                    };
                    setChatLines();
                } else {
                    Toast.makeText(getApplicationContext(), "No network connection available.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Get the text from the _editText widget. Checks against empty input.
    /*public void sendText(View v){
        if(_editText.getText().length()==0){
            Toast.makeText(this,"Can't have empty input",Toast.LENGTH_SHORT).show();
        }
        else{
            postData(_editText.getText().toString());
            _editText.setText("");
        }
    }*/
    //sets up the parameters to send to the server
    public HashMap<String, Object> setUpParams(HashMap<String, Object> params, String action) {
        params.put("action", action);
        params.put("role", _role);
        params.put("task", _task);
        params.put("workerId", "cb3c5a38b4999401ec88a7f8bf6bd90f");
        if (action != "post") {
            params.put("lastChatId", "-1");
        }
        return params;
    }

    //This sets the chat list so user can see all available chats.
    public void setChatLines() {
        String url = "http://128.237.179.10:8888/php/chatProcess.php";
        Map<String, Object> params = setUpParams(new HashMap<String, Object>(), "fetchNewChatRequester");
        AQuery aq = new AQuery(this);

        aq.ajax(url, params, JSONArray.class, new AjaxCallback<JSONArray>() {
            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                if (json != null) {
                    try {
                        for (int n = 0; n < json.length(); n++) {
                            String[] lineInfo = json.get(n).toString().split("\"");
                            ChatLineInfo chatLineInfo = _cli.setChatLineInfo(lineInfo, new ChatLineInfo());
                            _chatLineInfoArrayList.add(chatLineInfo);
                            _arrayList.add(chatLineInfo.get_role() + " : " + chatLineInfo.get_chatLine());
                        }
                        ((AdapterView<ListAdapter>) _chatList).setAdapter(_adapter);
                        _chatList.setSelection(_chatList.getCount() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                update();
            }
        });
    }

    /*
    Recursive function that constantly checks the server to see if there is a change in the chat.
     */
    public void update() {
        String url = "http://128.237.179.10:8888/php/chatProcess.php";
        AQuery aq = new AQuery(this);
        Map<String, Object> params = setUpParams(new HashMap<String, Object>(), "fetchNewChatRequester");

        aq.ajax(url, params, JSONArray.class, new AjaxCallback<JSONArray>() {
            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                if (json != null) {
                    if (json.length() > _chatLineInfoArrayList.size()) {
                        try {
                            String[] lineInfo = json.get(json.length() - 1).toString().split("\"");
                            ChatLineInfo chatLineInfo = _cli.setChatLineInfo(lineInfo, new ChatLineInfo());
                            _chatLineInfoArrayList.add(chatLineInfo);
                            _adapter.add(chatLineInfo.get_role() + " : " + chatLineInfo.get_chatLine());
                            if (_chatList.getAdapter() == null) {
                                ((AdapterView<ListAdapter>) _chatList).setAdapter(_adapter);
                            }
                            _chatList.setSelection(_chatList.getCount() - 1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (_canUpdate) {//in order to stop recursion once app is closed.
                    update();
                }
            }
        });
    }

    /*
    Sends the string to the server to add chat list.
     */
    public void postData(String words) {
        String url = "http://128.237.179.10:8888/php/chatProcess.php";
        AQuery aq = new AQuery(this);
        Map<String, Object> params = setUpParams(new HashMap<String, Object>(), "post");

        params.put("chatLine", words);
        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
            }
        });
    }

    //TODO: Set alarm manager to check for updates
    public void setAlarmManager() {
        _size = _chatLineInfoArrayList.size();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent broadcast_intent = new Intent(this, AlarmUpdateChatList1.class);
//        broadcast_intent.putExtra("ArrayList", _chatLineInfoArrayList.size());
//        broadcast_intent.putExtra("ChatNum",_task);
//        broadcast_intent.putExtra("Role", _role);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1234, broadcast_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), 100, pendingIntent);
    }

    public void stopAlarmManager() {
        Intent intentstop = new Intent(this, AlarmUpdateChatList1.class);
        PendingIntent senderstop = PendingIntent.getBroadcast(this,
                1234, intentstop, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManagerstop = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManagerstop.cancel(senderstop);
    }

    public void onStop() {//stops the recursion.
        super.onStop();
        _canUpdate = false;
        setAlarmManager();
    }

    public void onResume() {
        super.onResume();
        stopAlarmManager();
    }

    public void checkUpdate() {
        String url = "http://128.237.179.10:8888/php/chatProcess.php";
        AQuery aq = new AQuery(this);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("action", "fetchNewChatRequester");
        params.put("role", "crowd");
        params.put("task", "6");
        params.put("workerId", "cb3c5a38b4999401ec88a7f8bf6bd90f");
        params.put("lastChatId", "-1");
        _checkUpdate = false;
        aq.ajax(url, params, JSONArray.class, new AjaxCallback<JSONArray>() {
            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                update();
                if (json != null) {
                    if (json.length() > _chatLineInfoArrayList.size()) {
                        //notification
                        Intent intent = new Intent(getApplicationContext(), ChorusChat.class);
                        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_launcher).setAutoCancel(true)
                                .setContentIntent(pendingIntent).setWhen(System.currentTimeMillis())
                                .setGroup(NOTIFICATION_GROUP).setContentTitle("Chorus")
                                .setContentText("New Message");
                        NotificationManagerCompat nmc = NotificationManagerCompat.from(getApplicationContext());
                        nmc.notify(id++, notification.build());
                        _size = json.length();

                        chatText.setText(_cli.get_chatLine());
                    }
                }
            }
        });
    }

    public static class AlarmUpdateChatList1 extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            final ChorusChat c = new ChorusChat();
//            _size = intent.getExtras().getInt("ArrayList");
//            _task = intent.getStringExtra("ChatNum");
//            _role = intent.getStringExtra("Role");
            String url = "http://128.237.179.10:8888/php/chatProcess.php";
            AQuery aq = new AQuery(context);
            Map<String, Object> params = c.setUpParams(new HashMap<String, Object>(), "fetchNewChatRequester");
            params.put("role", c._role);
            params.put("task", c._task);
            aq.ajax(url, params, JSONArray.class, new AjaxCallback<JSONArray>() {
                @Override
                public void callback(String url, JSONArray json, AjaxStatus status) {
                    status.getMessage();
                    if (json.length() > c._size) {
                        //notification
                        Intent intent = new Intent(c.getApplicationContext(), ChorusChat.class);
                        final PendingIntent pendingIntent = PendingIntent.getActivity(c.getApplicationContext(),
                                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder notification = new NotificationCompat.Builder(c.getApplicationContext())
                                .setSmallIcon(R.drawable.ic_launcher).setAutoCancel(true)
                                .setContentIntent(pendingIntent).setWhen(System.currentTimeMillis())
                                .setGroup(c.NOTIFICATION_GROUP).setContentTitle("Chorus")
                                .setContentText("New Message");
                        NotificationManagerCompat nmc = NotificationManagerCompat.from(c.getApplicationContext());
                        nmc.notify(c.id++, notification.build());
                        c._size = json.length();

                        c.chatText.setText(c._cli.get_chatLine());
                    }
                }
            });
        }
    }

}