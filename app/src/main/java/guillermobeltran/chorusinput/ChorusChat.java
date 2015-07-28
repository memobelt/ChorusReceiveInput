package guillermobeltran.chorusinput;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import guillermobeltran.chorusinput.PushService.ParseUtils;

import static guillermobeltran.chorusinput.R.id.ChatList;
import static guillermobeltran.chorusinput.R.id.CrowdSend;
import static guillermobeltran.chorusinput.R.id.editText;
import static guillermobeltran.chorusinput.R.id.webView;

public class ChorusChat extends ActionBarActivity implements OnInitListener {
    EditText _editText;
    String _task, _role, _DBtask, _searchTerms;
    ListView _chatList;
    Button _crowdBtn;
    ImageButton _yelpBtn;
    ArrayList<ChatLineInfo> _chatLineInfoArrayList;
    ArrayList<String> _chatArrayList = new ArrayList<String>();
    ChatLineInfo _cli = new ChatLineInfo();
    ArrayAdapter _chatLineAdapter, _importantFactsAdapter;
    Boolean _canUpdate;
    DBHelper DbHelper;
    SQLiteDatabase chatdb;
    TextToSpeech myTTS;
    static String url = "https://talkingtothecrowd.org/Chorus/Chorus-New/";
    static String _chatUrl = url + "php/chatProcess.php";
    static String _searchUrl = "https://news.search.yahoo.com/search?p=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initializations
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chorus_chat);

        //get all the intents
        //workerId should be retrieved from intent here.
        _task = getIntent().getStringExtra("ChatNum");
        _role = getIntent().getStringExtra("Role");

        _chatList = (ListView) findViewById(ChatList);
        _editText = (EditText) findViewById(editText);
        _crowdBtn = (Button) findViewById(CrowdSend);
        _yelpBtn = (ImageButton) findViewById(R.id.yelp_button);
        _yelpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent yelp = new Intent(getApplicationContext(), SearchBarActivity.class);
                yelp.putExtra("taskId", _task);
                startActivity(yelp);
            }
        });
        _yelpBtn.setVisibility(View.GONE);
        _crowdBtn.setVisibility(View.VISIBLE);

        _canUpdate = true;
        _chatLineInfoArrayList = new ArrayList<ChatLineInfo>();//will hold all chatLines.

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //make sure TTS works
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, 200);

        _DBtask = "CHAT" + _task;//for the database that stores the messages.

        //displays the text in the ListView.
        _chatLineAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, _chatArrayList) {
            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                            /*YOUR CHOICE OF COLOR*/
                textView.setTextColor(Color.WHITE);
                textView.setAutoLinkMask(Linkify.ALL);
                textView.setText(Html.fromHtml(_chatArrayList.get(position).replace("\\", "")));

                return view;
            }
        };

        /*
        This either creates or opens the database for this chat. The cursor determines whether it
        has just been created or has been in place.
        */
        DbHelper = new DBHelper(getApplicationContext(), _DBtask);
        chatdb = DbHelper.getWritableDatabase();
        Cursor c = chatdb.rawQuery("SELECT * FROM " + _DBtask, null);

        if (networkInfo != null && networkInfo.isConnected()) {
            //only way for chat to work is to load the web page so this does it in invisible webview
            WebView webview = (WebView) findViewById(webView);
            webview.loadUrl(url + "chat-demo.php?task=" + _task);
            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);

            //This intent is if the Chat was opened from SpeakToMe so that means someone asked a
            //question so the question gets sent to the server.
            if (getIntent().getExtras().getBoolean("Asking")) {
                String words = getIntent().getStringExtra("Words");
                postData("chatLine", words, "post");
            }
            //intent from watch
            else if (getIntent().getExtras().getBoolean("Speech")) {
                _cli.set_role("requester");
                postData("chatLine", getIntent().getStringExtra("Input"), "post");
            }
            //post from Yelp
            else if (getIntent().getExtras().getBoolean("Yelp")) {
                _cli.set_role("crowd");
                _editText.setText(getIntent().getStringExtra("Words"));
            }
            //If the cursor from above does have items then the chat has been opened and we don't
            //have to call the server.
            if (c.getCount() > 0) {
                setChatLinesFromDB(c);
            } else {//we need to call the server and add the messages to the database.
                setChatLinesFromWeb();
            }

            ParseUtils.subscribeWithEmail(ParseUtils.customIdBuilder(_task));

        } else {
            if (c.getCount() > 0) {//no internet connection but maybe there are messages stored.
                setChatLinesFromDB(c);
            }
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    Get the text from the _editText widget. Checks against empty input.
     */
    //_crowdBtn's onClickListener to send text
    public void sendText(View v) {
        if (_editText.getText().length() == 0) {
            Toast.makeText(this, "Can't have empty input", Toast.LENGTH_SHORT).show();
        } else {

            String text = _editText.getText().toString();
            postData("chatLine", text, "post");
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("email", ParseUtils.customIdBuilder(_task));
            params.put("role", _role);
            params.put("task", _task);
            params.put("message", _editText.getText().toString());
            ParseCloud.callFunctionInBackground("sendPushToUser", params, new FunctionCallback<String>() {
                public void done(String success, ParseException e) {
                    if (e == null) {
                        Log.e("ChorusChat", "Push sent successfully.");
                    }
                }
            });
            //To send relevant Yahoo article
            if (text.toLowerCase().contains("news about")) {
                _searchTerms = text.substring(text.indexOf("news about") + "news about".length() + 1);
                new YahooNews().execute();
            }
            _editText.setText("");
        }
    }

    /*
    sets up the parameters to send to the server. Action is whether it's sending or fetching.
    LastChatId is for fetching messages.
    */
    public HashMap<String, Object> setUpParams(HashMap<String, Object> params, String action, String lastChatId) {
        params.put("action", action);
        params.put("role", _role);
        params.put("task", _task);
        params.put("workerId", "qq9t3ktatncj66geme1vdo31u5");
        if (action != "post") {
            params.put("lastChatId", lastChatId);
        }
        if (action == "fetchNewMemory") {
            params.put("lastMemoryId", "-1");
        }
        return params;
    }

    /*
    This sets the listviews lines from the database.
     */
    public void setChatLinesFromDB(Cursor c) {
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                ChatLineInfo cli = new ChatLineInfo();
                String role = c.getString(c.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry
                        .COLUMN_NAME_ROLE));
                cli.set_role(role);
                _cli.set_role(role);
                String msg = c.getString(c.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry
                        .COLUMN_NAME_MSG));
                cli.set_chatLine(msg.replace("\\", "")); //because HTML URL's have \'s
                _cli.set_chatLine(msg.replace("\\", ""));
                String id = c.getString(c.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry
                        .COLUMN_NAME_CHATID));
                cli.set_id(id);
                _cli.set_id(id);
                String time = c.getString(c.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry
                        .COLUMN_NAME_TIME));
                if (role.equals("requester")) {
                    cli.set_acceptedTime(time);
                    _cli.set_acceptedTime(time);
                } else {
                    cli.set_time(time);
                    _cli.set_time(time);
                }
                setUpArrayList(cli);
                c.moveToNext();
            }
        }
        displayMessages();
        update();
    }

    /*
    This sets the chat list so user can see all available chats.
     */
    public void setChatLinesFromWeb() {
        //-1 to retrieve all messages.
        Map<String, Object> params = setUpParams(new HashMap<String, Object>(), "fetchNewChatRequester", "-1");
        AQuery aq = new AQuery(this);

        aq.ajax(_chatUrl, params, JSONArray.class, new AjaxCallback<JSONArray>() {
            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                if (json != null) {
                    try {
                        for (int n = 0; n < json.length(); n++) {
                            //cuts up the JSON to be parsed.
                            int chatLineStart = json.get(n).toString().indexOf("chatLine\":\"");
                            int roleStart = json.get(n).toString().indexOf("\",\"role");
                            String json_string = json.get(n).toString().toString()
                                    .substring(chatLineStart + 11, roleStart);
                            String[] lineInfo = json.get(n).toString().split("\"");

                            ChatLineInfo chatLineInfo = _cli.setChatLineInfo(lineInfo, new ChatLineInfo());
                            chatLineInfo.set_chatLine(json_string);

                            //Values to add to DB.
                            ContentValues values = new ContentValues();
                            values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_ROLE, chatLineInfo.get_role());
                            values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_MSG,
                                    chatLineInfo.get_chatLine().replace("\\", ""));
                            values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_CHATID, chatLineInfo.get_id());
                            if (chatLineInfo.get_role().equals("requester")) {
                                values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_TIME,
                                        chatLineInfo.get_acceptedTime());
                            } else {
                                values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_TIME, chatLineInfo.get_time());
                            }
                            long newRowId = chatdb.insertOrThrow(_DBtask, null, values);
                            if (newRowId == -1) {//error check. should probably do more then Oh no.
                                Toast.makeText(getApplicationContext(), "Oh no", Toast.LENGTH_SHORT).show();
                            }
                            setUpArrayList(chatLineInfo);
                        }
                        displayMessages();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                update();
            }
        });
    }

    /*
    Sets up the ArrayList.
     */
    public void setUpArrayList(ChatLineInfo chatLineInfo) {
        _chatLineInfoArrayList.add(chatLineInfo);
        String temp = (chatLineInfo.get_chatLine()).toLowerCase();
        //whether the Yelp button should display to the crowd or not
        if (_role.equals("crowd")) { //Yelp button will show
            /*if (temp.contains("yelp") || temp.contains("food") || temp.contains("restaurant") ||
                    temp.contains(" eat"))*/
                _yelpBtn.setVisibility(View.VISIBLE);
            /*else { //Yelp button will not show
                _yelpBtn.setVisibility(View.GONE);
            }*/
        } else {
            _yelpBtn.setVisibility(View.GONE);
        }
        if (chatLineInfo.get_role().equals("requester")) {
            _chatArrayList.add(chatLineInfo.get_role() + " : " + chatLineInfo.get_chatLine() + " " +
                    getDate(chatLineInfo.get_acceptedTime()));
        } else {
            _chatArrayList.add(chatLineInfo.get_role() + " : " + chatLineInfo.get_chatLine() + " " +
                    getDate(chatLineInfo.get_time()));
        }
    }

    /*
    Display all the messages. Also initalizes the ability for the app to speak the lines.
     */
    public void displayMessages() {
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
    }

    /*
    Recursive function that constantly checks the server to see if there is a change in the chat
    along with notification functionality. Could probably be more efficient.
     */
    public void update() {
        AQuery aq = new AQuery(this);
        //Checks to see if there are more messages starting from last message.
        Map<String, Object> params = setUpParams(new HashMap<String, Object>(), "fetchNewChatRequester",
                _chatLineInfoArrayList.get(_chatLineInfoArrayList.size() - 1).get_id());

        aq.ajax(_chatUrl, params, JSONArray.class, new AjaxCallback<JSONArray>() {
            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                if (json != null) {
                    if (json.length() > 0) {
                        try {
                            //More parseing of JSON
                            int chatLineStart = json.get(json.length() - 1).toString().indexOf("chatLine\":\"");
                            int roleStart = json.get(json.length() - 1).toString().indexOf("\",\"role");
                            String json_string = json.get(json.length() - 1).toString()
                                    .substring(chatLineStart + 11, roleStart);
                            String[] lineInfo = json.get(json.length() - 1).toString().split("\"");

                            ChatLineInfo chatLineInfo = _cli.setChatLineInfo(lineInfo, new ChatLineInfo());
                            chatLineInfo.set_chatLine(json_string);

                            //Add to db.
                            ContentValues values = new ContentValues();
                            values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_ROLE, chatLineInfo.get_role());
                            values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_MSG,
                                    chatLineInfo.get_chatLine().replace("\\", ""));
                            values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_CHATID, chatLineInfo.get_id());
                            if (chatLineInfo.get_role().equals("requester")) {
                                values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_TIME,
                                        chatLineInfo.get_acceptedTime());
                            } else {
                                values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_TIME, chatLineInfo.get_time());
                            }
                            long newRowId = chatdb.insertOrThrow(_DBtask, null, values);
                            if (newRowId == -1) {
                                Toast.makeText(getApplicationContext(), "Oh no", Toast.LENGTH_SHORT).show();
                            }
                            setUpArrayList(chatLineInfo);

                            //If the Chat had no messages to begin with the adapter would not have
                            //been initialized in SetChatLines so we have to do it here.
                            if (_chatList.getAdapter() == null) {
                                displayMessages();
                            }

                            //speak the answer from crowd automatically
                            if (_role.equals("requester") && chatLineInfo.get_role().equals("crowd")) {
                                speakResults(chatLineInfo.get_chatLine());
                            }

                            Intent intent = new Intent(getApplicationContext(), OpenOnWatch.class);
                            intent.putExtra("Text", true);
                            intent.putExtra("ChatNum", _task);
                            intent.putExtra("Role", chatLineInfo.get_role());
                            intent.putExtra("Message", chatLineInfo.get_chatLine().replace("\\", ""));
                            if (chatLineInfo.get_role().equals("requester")) {
                                intent.putExtra("Time", chatLineInfo.get_acceptedTime());
                            } else {
                                intent.putExtra("Time", chatLineInfo.get_time());
                            }
                            startActivity(intent);

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
    Sends the string to the server to add. line is for whether the message goes in the Chat
    or the memory (important facts). Memory not implemented.
     */
    public void postData(String line, String words, String action) {
        AQuery aq = new AQuery(this);
        Map<String, Object> params = setUpParams(new HashMap<String, Object>(), action, null);

        params.put(line, words);
        if (action.equals("postMemory")) {
            aq.ajax(url + "php/memoryProcess.php", params, JSONObject.class,
                    new AjaxCallback<JSONObject>());
        } else {
            aq.ajax(_chatUrl, params, JSONObject.class, new AjaxCallback<JSONObject>());
        }
    }

    //for timestamp
    public String getDate(String s) {
        /*s format is yyyy-MM-d H:m:s, it is either the String result of get_time() (crowd) or
        get_acceptedTime() (requester) from chatLineInfo.
          */
        if (s == null) {
            return "";
        } else {
            String[] parsed_date = s.split(" ");
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-d H:mm:ss");
            String[] parsed_current = simpleDateFormat.format(date).split(" ");
            if (parsed_date[0].equals(parsed_current[0])) {
                return parsed_date[1].substring(0, parsed_date[1].length() - 3);
            } else {
                return s.substring(0, s.length() - 3);
            }
        }
    }

    /*
    Failed attempt at updating in background
     */
    public void setAlarmManager() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent broadcast_intent = new Intent(this, AlarmUpdateChatList.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1234, broadcast_intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), 1000, pendingIntent);
    }

    /*
    To stop updating
     */
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

    /*
    For testing and deleting db
     */
    public void deleteDB() {
//        db.delete(DatabaseContract.DatabaseEntry.TABLE_NAME, null, null);
//        db.rawQuery("DROP TABLE IF EXISTS " + DatabaseContract.DatabaseEntry.TABLE_NAME, null);
    }

    /*
    When user wants to input through voice.
     */
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
                    //from TTS to add to the text box.
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
                break;
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

    /*
    This is for the action bar. The icon for important facts.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chorus_chat, menu);
        String title = "Info";
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
//            getImportantFacts();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    Not functional. Important facts are not retrieved from server for unknown reason.
     */
    public void getImportantFacts() {
        View info = findViewById(R.id.info); // SAME ID AS MENU ID
        final PopupWindow popupWindow = new PopupWindow(this);
        LinearLayout layoutOfPopup = new LinearLayout(this);
        final ListView list = new ListView(this);

        // set some popup window properties
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
        popupWindow.showAsDropDown(info, 0, 0);

        _importantFactsAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, _chatArrayList) {
            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView = (TextView) view.findViewById(android.R.id.text1);

//                            YOUR CHOICE OF COLOR
                textView.setTextColor(Color.WHITE);

                return view;
            }
        };
        AQuery aq = new AQuery(this);
        Map<String, Object> params = setUpParams(new HashMap<String, Object>(), "fetchNewMemory", "-1");
        String memoryUrl = url + "php/memoryProcess.php";
        params.remove("role");
        aq.ajax(memoryUrl, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                status.getMessage();//this is where the important facts are supposed to come but no.
            }
        });
    }

    /*
    Split up the search term so it can be added to the yahoo link and work.
     */
    public String getSearchTerms(String search) {
        String[] s = search.split("\\s+");
        int len = s.length;
        String newSearch = "";
        for (int i = 1; i < len; i++) {
            newSearch = newSearch + s[i];
            if (i != len - 1) {
                newSearch = newSearch + "+";
            }
        }
        return newSearch;
    }

    /*
    Extract the url from the mess it comes in.
     */
    public String extractUrl(String html) {
        String[] href = html.split("\"");
        int len = href.length;
        for (int i = 0; i < len; i++) {
            if (href[i].contains("href")) {
                String searchUrl = href[i + 1];
                String news = searchUrl.substring(searchUrl.lastIndexOf("RU=") + 3,
                        searchUrl.lastIndexOf("/RK")).replace("%2f", "/").replace("%3a", ":");
                return news;
            }
        }
        return null;
    }

    /*
    Retrieves a link searched with Yahoo in the background.
     */
    class YahooNews extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Elements sections = Jsoup.connect(_searchUrl + getSearchTerms(_searchTerms))
                        .get().getElementsByTag("section");
                for (Element section : sections) {
                    //Looks for first article.
                    if (section.className().equals("dd algo fst NewsArticle")) {

                        //Send article to the server as a system message.
                        AQuery aq = new AQuery(getApplicationContext());
                        Map<String, Object> params1 = setUpParams(new HashMap<String, Object>(),
                                "post", null);
                        String chatLine = "You might be interested in this article about" +
                                _searchTerms + ":" +
                                "<br />" + " " +
                                extractUrl(section.child(0).child(0).html());
                        params1.put("chatLine", chatLine);
                        params1.put("role", "system");
                        aq.ajax(_chatUrl, params1, JSONObject.class, new AjaxCallback<JSONObject>());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
