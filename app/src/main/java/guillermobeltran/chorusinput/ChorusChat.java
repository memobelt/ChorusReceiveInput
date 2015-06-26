package guillermobeltran.chorusinput;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
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

import java.sql.SQLData;
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
    Boolean _canUpdate,_checkUpdate;
    int numNotifications,_size;
    static String _url = "http://128.237.184.183:8888/php/chatProcess.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initializations
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

            //get all the intents
            if (getIntent().getExtras().getBoolean("Asking")) {
                String words = getIntent().getStringExtra("Words");
                postData(words);
            }
            _task = getIntent().getStringExtra("ChatNum");
            _role = getIntent().getStringExtra("Role");
            //only way for chat to work is to load the webpage so this does it in invisible webview
            WebView webview = (WebView) findViewById(webView);
            webview.loadUrl("http://128.237.184.183:8888/chat.php?task=" + _task);
            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
//            webview.destroy();
            //make sure the text is white
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
            setChatLines();
        } else {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }
    //Get the text from the _editText widget. Checks against empty input.
    public void sendText(View v){
        if(_editText.getText().length()==0){
            Toast.makeText(this,"Can't have empty input",Toast.LENGTH_SHORT).show();
        }
        else{
            postData(_editText.getText().toString());
            _editText.setText("");
        }
    }
    //sets up the parameters to send to the server
    public  HashMap<String,Object> setUpParams(HashMap<String, Object> params, String action){
        params.put("action", action);
        params.put("role", _role);
        params.put("task", _task);
        params.put("workerId", "cb3c5a38b4999401ec88a7f8bf6bd90f");
        if(action != "post") {
            params.put("lastChatId", "-1");
        }
        return params;
    }
    //This sets the chat list so user can see all available chats.
    public void setChatLines(){
        Map<String, Object> params = setUpParams(new HashMap<String, Object>(), "fetchNewChatRequester");
        AQuery aq = new AQuery(this);

        aq.ajax(_url, params, JSONArray.class, new AjaxCallback<JSONArray>() {
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
    Recursive function that constantly checks the server to see if there is a change in the chat
    along with notification functionality.
     */
    public void update(){
        AQuery aq = new AQuery(this);
        Map<String, Object> params = setUpParams(new HashMap<String, Object>(), "fetchNewChatRequester");

        aq.ajax(_url, params, JSONArray.class, new AjaxCallback<JSONArray>() {
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

//                            numNotifications++;
//                            Intent viewIntent = new Intent(getApplicationContext(), ChorusChat.class);
//                            viewIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                            PendingIntent viewPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
//                                    viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
//                                    .setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Chorus").setAutoCancel(true)
//                                    .setWhen(System.currentTimeMillis()).setContentIntent(viewPendingIntent);
//                            if(numNotifications == 1) {
//                                mBuilder.setContentText("1 New Message");
//                            } else {
//                                mBuilder.setContentText(Integer.toString(numNotifications)+" New Messages");
//                            }
//                            NotificationManagerCompat nm = NotificationManagerCompat.from(getApplicationContext());
//                            nm.notify(0, mBuilder.build());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(_canUpdate) {//in order to stop recursion once app is closed.
                    update();
                }
            }
        });
    }
    /*
    Sends the string to the server to add chat list.
     */
    public void postData(String words) {
        AQuery aq = new AQuery(this);
        Map<String, Object> params = setUpParams(new HashMap<String, Object>(),"post");

        params.put("chatLine", words);
        aq.ajax(_url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
            }
        });
    }
    //TODO: Set alarm manager to check for updates
    public void setAlarmManager() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent broadcast_intent = new Intent(this, AlarmUpdateChatList.class);
//        broadcast_intent.putExtra("ArrayList", _chatLineInfoArrayList.size());
//        broadcast_intent.putExtra("ChatNum",_task);
//        broadcast_intent.putExtra("Role", _role);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1234, broadcast_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), 1000, pendingIntent);
    }
    public void stopAlarmManager(){
        Intent intentstop = new Intent(this, AlarmUpdateChatList.class);
        PendingIntent senderstop = PendingIntent.getBroadcast(this,
                1234, intentstop, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManagerstop = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManagerstop.cancel(senderstop);
    }
    public void onStop(){//stops the recursion.
        super.onStop();
        _canUpdate = false;
        insertToDB();
//        setAlarmManager();
    }
    public void onResume(){
        super.onResume();
        stopAlarmManager();
//        deleteDB();
    }
    public void deleteDB(){
        DBHelper mDbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(DatabaseContract.DatabaseEntry.TABLE_NAME,null,null);
        db.rawQuery("DROP TABLE IF EXISTS " + DatabaseContract.DatabaseEntry.TABLE_NAME, null);
        db.close();
        mDbHelper.close();
    }
    public void insertToDB(){
        DBHelper mDbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String[] projection = {
                DatabaseContract.DatabaseEntry.COLUMN_NAME_TASK
        };
        String selection = DatabaseContract.DatabaseEntry.COLUMN_NAME_TASK + "=?";
        String[] selectionArgs = { _task };

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_ROLE, _role);
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_TASK, _task);
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_SIZE, _chatLineInfoArrayList.size());
        int i = db.update(DatabaseContract.DatabaseEntry.TABLE_NAME,values,"task = "+_task,null);
        if(i==0){
            long newRowId = db.insertOrThrow(DatabaseContract.DatabaseEntry.TABLE_NAME, null, values);
            if (newRowId == -1){
                Toast.makeText(this,"Oh no",Toast.LENGTH_SHORT).show();
            }
        }
//        cursor = db.query(DatabaseContract.DatabaseEntry.TABLE_NAME,null,null,null,null,null,null);
//        cursor.getCount();
        db.close();
        mDbHelper.close();
//        cursor.close();
    }
}
