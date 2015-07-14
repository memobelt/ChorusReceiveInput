package guillermobeltran.chorusinput;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ChorusChat extends Activity {
    String _task, _role;
    Button send;
    Spinner spinner;
    TextView mTextView, chatText;
    ArrayList<ChatLineInfo> _chatLineInfoArrayList;
    ArrayList<String> _chatArrayList = new ArrayList<String>();
    ChatLineInfo _cli = new ChatLineInfo();
    ArrayAdapter _chatLineAdapter;
    Boolean _canUpdate, _checkUpdate;
    int _size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initializations
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chorus_chat);
        _canUpdate = true;
        _chatLineInfoArrayList = new ArrayList<ChatLineInfo>();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                chatText = (TextView) findViewById(R.id.TextArea);
                send = (Button) findViewById(R.id.send_button);

                //suggested responses
                spinner = (Spinner) findViewById(R.id.spinner);
                spinner.setPrompt("Reply...");
                if (chatText.getText().toString().contains("?")) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.question_array, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                } else {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.response_array, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                }
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(final AdapterView<?> parent, View view, final int position, long id) {
                        if (parent.getItemAtPosition(position).equals("Custom response")) {
                            Intent intent = new Intent(getApplicationContext(), Microphone.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else if (parent.getItemAtPosition(position).equals("Reply...") == false) {
                            send.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), OpenOnPhone.class);
                                    intent.putExtra("Response", parent.getItemAtPosition(position).toString());
                                    intent.putExtra("caller", "Response");
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                if (getIntent().getStringExtra("caller").equals("ListenerServiceFromPhone")) {
                    chatText.setText(getIntent().getStringExtra("New Text"));
                    if (chatText.getText().toString().contains("?")) {
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.question_array, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                    } else {
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.response_array, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    }

                    //so ChorusChat doesn't open everytime a new message is posted
                    finish();
                } /*else if (getIntent().getStringExtra("caller").equals("ListenerUpdate")) {
                    chatText.setText(getIntent().getStringExtra("New Text"));
                    if (chatText.getText().toString().contains("?")) {
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.question_array, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                    } else {
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.response_array, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    }
                } else if (getIntent().getStringExtra("caller").equals("MainActivity")){
                    //get last string in chat
                    Intent update = new Intent(getApplicationContext(), OpenOnPhone.class);
                    update.putExtra("caller", "Update");
                    startActivity(update);
                }*/
            }
        });
        setChatLines();
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
            chatText.setText(_cli.get_chatLine());
            update();

    }

    /*
    Recursive function that constantly checks the server to see if there is a change in the chat.
     */
    public void update() {

        Map<String, Object> params = setUpParams(new HashMap<String, Object>(), "fetchNewChatRequester");
        chatText.setText(_cli.get_chatLine());
        if (_canUpdate) {//in order to stop recursion once app is closed.
            update();
        }
    }

    /*
    Sends the string to the server to add chat list.
     */
    public void postData(String line, String words, String action) {
        AQuery aq = new AQuery(this);
        Map<String, Object> params = setUpParams(new HashMap<String, Object>(), action);
        params.put(line, words);
        chatText.setText(words);
        /*if (action.equals("fetchNewMemory")) {
            aq.ajax(url + "php/memoryProcess.php", params, JSONObject.class,
                    new AjaxCallback<JSONObject>());
        } else {
            aq.ajax(_chatUrl, params, JSONObject.class, new AjaxCallback<JSONObject>());

            Intent intent = new Intent(getApplicationContext(), OpenOnPhone.class);
            intent.putExtra("Update", false);
            intent.putExtra("Message", _cli.get_role() + " : " + words);
            startActivity(intent);
        }*/
    }
    public void sendText(View v) {
        if (chatText.getText().length() == 0) {
            Toast.makeText(this, "Can't have empty input", Toast.LENGTH_SHORT).show();
        } else {
            postData("chatLine", chatText.getText().toString(), "post");
        }
    }

    //TODO: Set alarm manager to check for updates
    public void setAlarmManager() {
        _size = _chatLineInfoArrayList.size();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent broadcast_intent = new Intent(this, AlarmUpdateChatList.class);
//        broadcast_intent.putExtra("ArrayList", _chatLineInfoArrayList.size());
//        broadcast_intent.putExtra("ChatNum",_task);
//        broadcast_intent.putExtra("Role", _role);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1234, broadcast_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), 100, pendingIntent);
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
        setAlarmManager();
    }

    public void onResume() {
        super.onResume();
        stopAlarmManager();
    }
}