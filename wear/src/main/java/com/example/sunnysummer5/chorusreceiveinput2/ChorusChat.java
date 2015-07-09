package com.example.sunnysummer5.chorusreceiveinput2;

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
        //implements TextToSpeech.OnInitListener {
    String _task, _role;
    Button send, onPhone;
    Spinner spinner;
    TextView mTextView, chatText;
    ArrayList<ChatLineInfo> _chatLineInfoArrayList;
    ArrayList<String> _arrayList = new ArrayList<String>();
    ChatLineInfo _cli = new ChatLineInfo();
    ArrayAdapter _adapter;
    Boolean _canUpdate, _checkUpdate;
    int _size;
    //TextToSpeech myTTS;

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

                /*Intent checkTTSIntent = new Intent();
                checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
                startActivityForResult(checkTTSIntent, 200); */
                chatText = (TextView) findViewById(R.id.TextArea);
                /*chatText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        speakResults(chatText.getText().toString());
                    }
                });*/

                send = (Button) findViewById(R.id.send_button);
                onPhone = (Button) findViewById(R.id.next_button);
                onPhone.setText("Open on Phone");
                onPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent open = new Intent(getApplicationContext(), OpenOnPhone.class);
                        open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        open.putExtra("caller", "ChorusChat");
                        startActivity(open);
                    }
                });
                onPhone.setVisibility(View.GONE);

                //generated responses
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
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else if (parent.getItemAtPosition(position).equals("Reply...") == false) {
                            send.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), OpenOnPhone.class);
                                    intent.putExtra("Response", parent.getItemAtPosition(position).toString());
                                    intent.putExtra("caller", "Response");
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                    if(getIntent().getExtras().getBoolean("system")) {
                        onPhone.setVisibility(View.VISIBLE);
                    }
                    else {
                        onPhone.setVisibility(View.GONE);
                    }
                    //so ChorusChat doesn't open everytime a new message is posted
                    finish();
                }

            }
        });
        setChatLines();
    }

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
                        //((AdapterView<ListAdapter>) _chatList).setAdapter(_adapter);
                        //_chatList.setSelection(_chatList.getCount() - 1);
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
                            /*if (_chatList.getAdapter() == null) {
                                ((AdapterView<ListAdapter>) _chatList).setAdapter(_adapter);
                            }
                            _chatList.setSelection(_chatList.getCount() - 1);*/

                            //spinner automatic answers
                            if (chatLineInfo.get_chatLine().contains("?")) {
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
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
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
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            myTTS.setLanguage(Locale.US);
        }
        else if (status == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_SHORT).show();
        }
    }
    public void speakResults(String words){
        myTTS.speak(words, TextToSpeech.QUEUE_FLUSH, null);
    } */
}