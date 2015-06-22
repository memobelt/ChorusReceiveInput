package guillermobeltran.chorusinput;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

import static guillermobeltran.chorusinput.R.id.ChatList;
import static guillermobeltran.chorusinput.R.id.editText;
import static guillermobeltran.chorusinput.R.id.webView;


public class ChorusChat extends Activity {
    EditText _editText;
    String _task, _role;
    ListView _chatList;
    ArrayList<ChatLineInfo> _chatLineInfoArrayList;
    ArrayList<String> _arrayList = new ArrayList<String>();
    ChatLineInfo _cli = new ChatLineInfo();
    ArrayAdapter _adapter;
    Boolean _canUpdate;
    int numNotifications;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chorus_chat);
        _chatList = (ListView) findViewById(ChatList);
        _editText = (EditText) findViewById(editText);
        _canUpdate = true;

        _chatLineInfoArrayList = new ArrayList<ChatLineInfo>();
        numNotifications=0;
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

                WebView webview = (WebView) findViewById(webView);//load the webpage for chat
                webview.loadUrl("http://128.237.179.10:8888/chat.php?task=" + _task);
                WebSettings webSettings = webview.getSettings();
                webSettings.setJavaScriptEnabled(true);

            if (getIntent().getExtras().getBoolean("Asking")) {
                String words = getIntent().getStringExtra("Words");
                postData(words, _task, _role, this);
            }
            _task = getIntent().getStringExtra("ChatNum");
            _role = getIntent().getStringExtra("Role");

            _adapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1, _arrayList){
                @Override
                public View getView(int position, View convertView,
                                    ViewGroup parent) {
                    View view =super.getView(position, convertView, parent);

                    TextView textView=(TextView) view.findViewById(android.R.id.text1);

                            /*YOUR CHOICE OF COLOR*/
                    textView.setTextColor(Color.WHITE);

                    return view;
                }
            };
            setChat();
//            _handler = new Handler();
//            _handler.postDelayed(_updateChatList, 1000);
        } else {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendText(View v){
        if(_editText.getText().length()==0){
            Toast.makeText(this,"Can't have empty input",Toast.LENGTH_SHORT).show();
        }
        postData(_editText.getText().toString(), _task, _role, this);
        _editText.setText("");
    }
    public void setAlarmManager() {
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        Intent broadcast_intent = new Intent(this, AlarmUpdateChatList.class);
//        broadcast_intent.putExtra("ArrayList", _chatLineInfoArrayList.size());
////            broadcast_intent.putExtra("")
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, broadcast_intent, 0);
//        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime(), 100, pendingIntent);
    }
    public void setChat(){
        String url = "http://128.237.179.10:8888/php/chatProcess.php";
        Map<String, Object> params = setUpParams(new HashMap<String, Object>(), "fetchNewChatRequester",
                _role, _task);
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
    public HashMap<String,Object> setUpParams(HashMap<String, Object> params, String action, String role, String task){
        params.put("action", action);
        params.put("role", _role);
        params.put("task", _task);
        params.put("workerId", "cb3c5a38b4999401ec88a7f8bf6bd90f");
        if(action != "post") {
            params.put("lastChatId", "-1");
        }
        return params;
    }
    public void postData(String words, String task, String role, Activity activity) {
        String url = "http://128.237.179.10:8888/php/chatProcess.php";
        AQuery aq = new AQuery(this);
        Map<String, Object> params = setUpParams(new HashMap<String, Object>(),"post",
                _role,_task);

        params.put("chatLine", words);
        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                status.getError();
                status.getMessage();
            }
        });
    }
    public void onStop(){
        super.onStop();
        _canUpdate = false;
    }
    public void update(){
//        Toast.makeText(getApplicationContext(), "Hm", Toast.LENGTH_SHORT).show();
        String url = "http://128.237.179.10:8888/php/chatProcess.php";
        AQuery aq = new AQuery(this);
        Map<String, Object> params = setUpParams(new HashMap<String, Object>(), "fetchNewChatRequester",
                _role, _task);

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
                            if(_chatList.getAdapter()==null){
                                ((AdapterView<ListAdapter>) _chatList).setAdapter(_adapter);
                            }
                            _chatList.setSelection(_chatList.getCount() - 1);

                            numNotifications++;
                            Intent viewIntent = new Intent(getApplicationContext(), ChorusChat.class);
                            viewIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            PendingIntent viewPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                                    viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Chorus").setAutoCancel(true)
                                    .setWhen(System.currentTimeMillis()).setContentIntent(viewPendingIntent);
                            if(numNotifications == 1) {
                                mBuilder.setContentText("1 New Message");
                            } else {
                                mBuilder.setContentText(Integer.toString(numNotifications)+" New Messages");
                            }
                            NotificationManagerCompat nm = NotificationManagerCompat.from(getApplicationContext());
                            nm.notify(0, mBuilder.build());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(_canUpdate) {
                    update();
                }
            }
        });
    }
}
