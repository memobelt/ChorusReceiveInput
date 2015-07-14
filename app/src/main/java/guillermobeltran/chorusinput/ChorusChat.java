package guillermobeltran.chorusinput;

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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
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


public class ChorusChat extends ActionBarActivity implements OnInitListener {
    EditText _editText;
    String _task, _role;
    ListView _chatList;
    Button _crowdBtn, _yelpBtn;
    ArrayList<ChatLineInfo> _chatLineInfoArrayList;
    ArrayList<String> _chatArrayList = new ArrayList<String>();
    ArrayList<String> _factsArrayList = new ArrayList<String>();
    ChatLineInfo _cli = new ChatLineInfo();
    ArrayAdapter _chatLineAdapter, _importantFactsAdapter;
    Boolean _canUpdate;
    TextToSpeech myTTS;
    static String url = "https://talkingtothecrowd.org/Chorus/Chorus-New/";
    static String _chatUrl = url + "php/chatProcess.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initializations
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chorus_chat);
        _chatList = (ListView) findViewById(ChatList);
        _editText = (EditText) findViewById(editText);
        _crowdBtn = (Button) findViewById(CrowdSend);
        _yelpBtn = (Button) findViewById(R.id.yelp_button);
        _yelpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent yelp = new Intent(getApplicationContext(), SearchBarActivity.class);
                yelp.putExtra("taskId", _task);
                startActivity(yelp);
            }
        });
        _yelpBtn.setVisibility(View.GONE);

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
            webview.loadUrl(url + "chat-demo.php?task=" + _task);
            //webview.loadUrl(url+"requester-demo.php?task="+_task);
            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            //make sure the text is white
            _chatLineAdapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1, _chatArrayList) {
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
            //intent from phone
            if (getIntent().getExtras().getBoolean("Asking")) {
                _cli.set_role("requester");
                String words = getIntent().getStringExtra("Words");
                postData("chatLine", words, "post");
            }
            //intent from watch
            else if (getIntent().getExtras().getBoolean("Speech")) {
                _cli.set_role("requester");
                postData("chatLine", getIntent().getStringExtra("Input"), "post");
                //setChatLines();
                //finish();
            }
            //watch just opened "Review" and needs update
            /*else if(getIntent().getExtras().getBoolean("Update")) {
                Map<String, Object> params = setUpParams(new HashMap<String, Object>(), "fetchNewChatRequester");
                AQuery aq = new AQuery(this);

                aq.ajax(_chatUrl, params, JSONArray.class, new AjaxCallback<JSONArray>() {
                    @Override
                    public void callback(String url, JSONArray json, AjaxStatus status) {
                        if (json != null) {
                            try {
                                for (int n = 0; n < json.length(); n++) {
                                    String[] lineInfo = json.get(n).toString().split("\"");
                                    ChatLineInfo chatLineInfo = _cli.setChatLineInfo(lineInfo, new ChatLineInfo());
                                    if (chatLineInfo.get_chatLine().contains("http") ||
                                            chatLineInfo.get_chatLine().contains("www.")) {
                                        chatLineInfo.set_chatLine(Html.fromHtml(chatLineInfo.get_chatLine()).toString());
                                    }
                                    Intent intent = new Intent(getApplicationContext(), OpenOnWatch.class);
                                    intent.putExtra("Update", true);
                                    intent.putExtra("Message", chatLineInfo.get_role() + " : " + chatLineInfo.get_chatLine());
                                    startActivity(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                finish();
            }*/
            setChatLines();
        } else {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    //Get the text from the _editText widget. Checks against empty input.
    public void sendText(View v) {
        if (_editText.getText().length() == 0) {
            Toast.makeText(this, "Can't have empty input", Toast.LENGTH_SHORT).show();
        } else {
            postData("chatLine", _editText.getText().toString(), "post");
            _editText.setText("");
        }
    }

    //sets up the parameters to send to the server
    public HashMap<String, Object> setUpParams(HashMap<String, Object> params, String action) {
        params.put("action", action);
        params.put("role", _role);
        params.put("task", _task);
        params.put("workerId", "qq9t3ktatncj66geme1vdo31u5");
        if (action != "post") {
            params.put("lastChatId", "-1");
        }
        if (action == "fetchNewMemory") {
            params.put("lastMemoryId", "932");
        }
        return params;
    }

    //This sets the chat list so user can see all available chats.
    public void setChatLines() {
        Map<String, Object> params = setUpParams(new HashMap<String, Object>(), "fetchNewChatRequester");
        AQuery aq = new AQuery(this);

        aq.ajax(_chatUrl, params, JSONArray.class, new AjaxCallback<JSONArray>() {
            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                if (json != null) {
                    try {
                        for (int n = 0; n < json.length(); n++) {
                            String[] lineInfo = json.get(n).toString().split("\"");
                            ChatLineInfo chatLineInfo = _cli.setChatLineInfo(lineInfo, new ChatLineInfo());
                            _chatLineInfoArrayList.add(chatLineInfo);
                            if (chatLineInfo.get_chatLine().contains("http") ||
                                    chatLineInfo.get_chatLine().contains("www.")) {
                                chatLineInfo.set_chatLine(Html.fromHtml(chatLineInfo.get_chatLine()).toString());
                            }
                            if (chatLineInfo.get_chatLine().toString().contains("Yelp") &&
                                    chatLineInfo.get_role().equals("requester") && _role.equals("crowd")) {
                                _yelpBtn.setVisibility(View.VISIBLE);
                            } else {
                                _yelpBtn.setVisibility(View.GONE);
                            }
                            _chatArrayList.add(chatLineInfo.get_role() + " : " + chatLineInfo.get_chatLine());
                        }
                        ((AdapterView<ListAdapter>) _chatList).setAdapter(_chatLineAdapter);
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
    public void update() {
        AQuery aq = new AQuery(this);
        Map<String, Object> params = setUpParams(new HashMap<String, Object>(), "fetchNewChatRequester");

        aq.ajax(_chatUrl, params, JSONArray.class, new AjaxCallback<JSONArray>() {
            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                if (json != null) {
                    if (json.length() > _chatLineInfoArrayList.size()) {
                        try {
                            String[] lineInfo = json.get(json.length() - 1).toString().split("\"");
                            ChatLineInfo chatLineInfo = _cli.setChatLineInfo(lineInfo, new ChatLineInfo());
                            _chatLineInfoArrayList.add(chatLineInfo);
                            _chatLineAdapter.add(chatLineInfo.get_role() + " : " + chatLineInfo.get_chatLine());
                            if (chatLineInfo.get_chatLine().toString().contains("Yelp") &&
                                    chatLineInfo.get_role().equals("requester") && _role.equals("crowd")) {
                                _yelpBtn.setVisibility(View.VISIBLE);
                            } else {
                                _yelpBtn.setVisibility(View.GONE);
                            }

                            if (_chatList.getAdapter() == null) {
                                ((AdapterView<ListAdapter>) _chatList).setAdapter(_chatLineAdapter);
                                _chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        speakResults(_chatLineInfoArrayList.get(position).get_chatLine());
                                    }
                                });
                            }

                            _chatList.setSelection(_chatList.getCount() - 1);
                            if (_role == "requester" && chatLineInfo.get_role() == "crowd") {
                                speakResults(chatLineInfo.get_chatLine());
                            }

                            int numNotifications = json.length() - _chatLineInfoArrayList.size();
                            Intent viewIntent = new Intent(getApplicationContext(), ChorusChat.class);
                            viewIntent.putExtra("ChatNum", _task);
                            viewIntent.putExtra("Role", _role);
                            viewIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                            PendingIntent viewPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                                    viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Chorus").setAutoCancel(true)
                                    .setWhen(System.currentTimeMillis()).setContentIntent(viewPendingIntent);
                            mBuilder.setContentText(Integer.toString(numNotifications) + " New Messages " +
                                    "in Chat " + _task);
                            NotificationManagerCompat nm = NotificationManagerCompat.from(getApplicationContext());
                            nm.notify(001, mBuilder.build());

                            Intent intent = new Intent(getApplicationContext(), OpenOnWatch.class);
                            intent.putExtra("Update", false);
                            intent.putExtra("Message", chatLineInfo.get_role() + " : " + chatLineInfo.get_chatLine());
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (_canUpdate) {//in order to stop recursion once app is closed.
                    int size = _chatLineInfoArrayList.size();
                    if (size > 2) {
                        if ("crowd".equals(_chatLineInfoArrayList.get(size - 1).get_role()) &&
                                "crowd".equals(_chatLineInfoArrayList.get(size - 2).get_role()) &&
                                "crowd".equals(_role)) {
                            _crowdBtn.setVisibility(View.INVISIBLE);
                        } else {
                            _crowdBtn.setVisibility(View.VISIBLE);
                        }
                    }
                    update();
                }
            }
        });
    }

    /*
    Sends the string to the server to add chat list.
     */
    public void postData(String line, String words, String action) {
        AQuery aq = new AQuery(this);
        Map<String, Object> params = setUpParams(new HashMap<String, Object>(), action);

        params.put(line, words);
        if (action.equals("fetchNewMemory")) {
            aq.ajax(url + "php/memoryProcess.php", params, JSONObject.class,
                    new AjaxCallback<JSONObject>());
        } else {
            aq.ajax(_chatUrl, params, JSONObject.class, new AjaxCallback<JSONObject>());

            Intent intent = new Intent(getApplicationContext(), OpenOnWatch.class);
            intent.putExtra("Update", false);
            intent.putExtra("Message", _cli.get_role() + " : " + words);
            startActivity(intent);
        }
    }

    public void setAlarmManager() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent broadcast_intent = new Intent(this, AlarmUpdateChatList.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1234, broadcast_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), 1000, pendingIntent);
    }

    public void stopAlarmManager() {
        Intent intentstop = new Intent(this, AlarmUpdateChatList.class);
        PendingIntent senderstop = PendingIntent.getBroadcast(this,
                1234, intentstop, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManagerstop = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManagerstop.cancel(senderstop);
    }

    public void onStop() {//stops the recursion.
        super.onStop();
        _canUpdate = false;
        //insertToDB();
//        setAlarmManager();
    }

    /*public void onResume(){
        super.onResume();
        stopAlarmManager();
//        deleteDB();
    }*/
    public void deleteDB() {
        DBHelper mDbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(DatabaseContract.DatabaseEntry.TABLE_NAME, null, null);
        db.rawQuery("DROP TABLE IF EXISTS " + DatabaseContract.DatabaseEntry.TABLE_NAME, null);
        db.close();
        mDbHelper.close();
    }

    public void insertToDB() {
        DBHelper mDbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_ROLE, _role);
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_TASK, _task);
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_SIZE, _chatLineInfoArrayList.size());
        int i = db.update(DatabaseContract.DatabaseEntry.TABLE_NAME, values, "task = " + _task, null);
        if (i == 0) {
            long newRowId = db.insertOrThrow(DatabaseContract.DatabaseEntry.TABLE_NAME, null, values);
            if (newRowId == -1) {
                Toast.makeText(this, "Oh no", Toast.LENGTH_SHORT).show();
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
                } else {
                    Intent installTTSIntent = new Intent();
                    installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installTTSIntent);
                }
            }
        }
    }

    public void speakResults(String words) {
        myTTS.speak(words, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            myTTS.setLanguage(Locale.US);
        } else if (status == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chorus_chat, menu);
        String title = "Wat";
        int groupid = R.id.info;
        int itemId = R.id.info;
        int order = 100;
        menu.add(groupid, itemId, order, title);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.info) {
            //getImportantFacts();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /*public void getImportantFacts(){
        View info = findViewById(R.id.info); // SAME ID AS MENU ID
        final PopupWindow popupWindow = new PopupWindow(this);
        LinearLayout layoutOfPopup = new LinearLayout(this);
        final ListView list = new ListView(this);

        // set some pupup window properties
        popupWindow.setFocusable(true);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        // set your group view as a popup window content
        TextView popupText = new TextView(this);
        popupText.setText("This is Popup Window.press OK to dismiss it.");
        popupText.setPadding(0, 0, 0, 20);
        popupText.setTextColor(Color.WHITE);

        layoutOfPopup.setOrientation(LinearLayout.VERTICAL);
        layoutOfPopup.addView(popupText);
        layoutOfPopup.setBackgroundColor(Color.BLACK);

        popupWindow.setContentView(layoutOfPopup);

        // This will allow you to close window by clickin not in its area
        popupWindow.setOutsideTouchable(true);
        // Show the window at desired place. The first argument is a control, wich will be used to place window... defining dx and dy will shift the popup window
        popupWindow.showAsDropDown(info,0,0);

        _importantFactsAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, _chatArrayList){
            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View view =super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);*/

                            /*YOUR CHOICE OF COLOR*/
                /*textView.setTextColor(Color.WHITE);

                return view;
            }
        };
        AQuery aq = new AQuery(this);
        Map<String, Object> params = setUpParams(new HashMap<String, Object>(), "postMemory");
        params.put("memoryLine","proxy proxy");
        String memoryUrl = url +"php/memoryProcess.php";
        aq.ajax(memoryUrl, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                status.getMessage();
            }
        });
    }*/
}
