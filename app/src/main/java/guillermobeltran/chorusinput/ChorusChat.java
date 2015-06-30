package guillermobeltran.chorusinput;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.Locale;
import java.util.Map;

import static guillermobeltran.chorusinput.R.id.ChatList;
import static guillermobeltran.chorusinput.R.id.CrowdSend;
import static guillermobeltran.chorusinput.R.id.editText;
import static guillermobeltran.chorusinput.R.id.webView;


public class ChorusChat extends Activity implements OnInitListener {
    EditText _editText;
    String _task, _role;
    ListView _chatList;
    Button _crowdBtn;
    ArrayList<ChatLineInfo> _chatLineInfoArrayList;
    ArrayList<String> _arrayList = new ArrayList<String>();
    ChatLineInfo _cli = new ChatLineInfo();
    ArrayAdapter _adapter;
    Boolean _canUpdate;
    TextToSpeech myTTS;
    static String _url = "http://128.237.179.70:8888/php/chatProcess.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initializations
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chorus_chat);
        _chatList = (ListView) findViewById(ChatList);
        _editText = (EditText) findViewById(editText);
        _crowdBtn = (Button) findViewById(CrowdSend);
        _canUpdate = true;
        _chatLineInfoArrayList = new ArrayList<ChatLineInfo>();
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, 200);
        if (networkInfo != null && networkInfo.isConnected()) {

            //get all the intents

            _task = getIntent().getStringExtra("ChatNum");
            _role = getIntent().getStringExtra("Role");
            //only way for chat to work is to load the webpage so this does it in invisible webview
            WebView webview = (WebView) findViewById(webView);
            webview.loadUrl("http://128.237.179.70:8888/chat.php?task=" + _task);
            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webview.destroy();
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
            if (getIntent().getExtras().getBoolean("Asking")) {
                String words = getIntent().getStringExtra("Words");
                postData(words);
            }
            else if(getIntent().getExtras().getBoolean("Speech")) {
                postData(getIntent().getStringExtra("Input"));
            }
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
                        _chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                Toast.makeText(getApplicationContext(),Integer.toString(position)+
//                                        " "+ Long.toString(id),Toast.LENGTH_SHORT).show();
                                speakResults(_chatLineInfoArrayList.get(position).get_chatLine());
                            }
                        });
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
                            _chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                        Toast.makeText(getApplicationContext(),Integer.toString(position)+
//                                                " "+ Long.toString(id),Toast.LENGTH_SHORT).show();
                                    speakResults(_chatLineInfoArrayList.get(position).get_chatLine());
                                }
                            });
                        }
                        _chatList.setSelection(_chatList.getCount() - 1);
                        if(_role=="requester"&&chatLineInfo.get_role()=="crowd"){
                            speakResults(chatLineInfo.get_chatLine());
                        }
                        int size = _chatLineInfoArrayList.size();
                        if(_chatLineInfoArrayList.get(size-1).get_role()=="crowd"&&
                                _chatLineInfoArrayList.get(size-2).get_role()=="crowd"){
                            _crowdBtn.setVisibility(View.INVISIBLE);
                        }
                        else{
                            _crowdBtn.setVisibility(View.VISIBLE);
                        }
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
        Map<String, Object> params = setUpParams(new HashMap<String, Object>(), "post");

        params.put("chatLine", words);
        aq.ajax(_url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                status.getMessage();
            }
        });
    }
    public void setAlarmManager() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent broadcast_intent = new Intent(this, AlarmUpdateChatList.class);
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
        db.close();
        mDbHelper.close();
    }

    public void ChatSpeechInput(View v) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && data != null) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    _editText.setText(result.get(0));
                }
                break;
            }
            case 200: {
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    myTTS = new TextToSpeech(this, this);
                }
                else {
                    Intent installTTSIntent = new Intent();
                    installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installTTSIntent);
                }
            }
        }
    }
    public void speakResults(String words){
        myTTS.speak(words,TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            myTTS.setLanguage(Locale.US);
        }
        else if (status == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_SHORT).show();
        }
    }
}
