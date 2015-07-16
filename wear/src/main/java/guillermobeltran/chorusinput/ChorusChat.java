package guillermobeltran.chorusinput;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class ChorusChat extends Activity {
    String _task, _role, _DBtask;
    Button send;
    Spinner spinner;
    TextView mTextView, chatText;
    ArrayList<ChatLineInfo> _chatLineInfoArrayList;
    ArrayList<String> _chatArrayList = new ArrayList<String>();
    ChatLineInfo _cli = new ChatLineInfo();
    ArrayAdapter _chatLineAdapter;
    Boolean _canUpdate, _checkUpdate;
    int _size;
    DBHelper DbHelper;
    SQLiteDatabase chatdb;
    Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initializations
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chorus_chat);
        _canUpdate = true;
        _chatLineInfoArrayList = new ArrayList<ChatLineInfo>();

        _task = getIntent().getStringExtra("ChatNum");
        _DBtask = "CHAT" + _task;
        DbHelper = new DBHelper(getApplicationContext(), _DBtask);
        chatdb = DbHelper.getWritableDatabase();
        c = chatdb.rawQuery("SELECT * FROM " + _DBtask, null);

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
                                    postData(parent.getItemAtPosition(position).toString());
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
                        send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getApplicationContext(), "Can't have empty input",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                 /*else if (getIntent().getStringExtra("caller").equals("ListenerUpdate")) {
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
                if (getIntent().getStringExtra("caller").equals("ListenerServiceFromPhone")) {
                    Log.i("test", "listener");
                    setChatLinesFromPhone();
                } else {
                    Log.i("test", "else1");
                    if (c.getCount() > 0) {
                        Log.i("test", "else2");
                        //setChatLinesFromDB(c);
                        update();
                    }
                }
            }
        });

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

    public void setChatLinesFromDB(Cursor c) {
        if (c.moveToFirst()) {
            Log.i("test", "here1");
            ChatLineInfo cli = new ChatLineInfo();
            while (!c.isAfterLast()) {
                Log.i("test", "here2");
                String msg = c.getString(c.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry
                        .COLUMN_NAME_MSG));
                cli.set_chatLine(msg);
                String id = c.getString(c.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry
                        .COLUMN_NAME_CHATID));
                cli.set_id(id);
                c.moveToNext();
            }
            chatText.setText(cli.get_chatLine());
        }
        //setChatLinesFromPhone();
        if (_canUpdate)
            update();
    }

    public void setChatLinesFromPhone() {
        Log.i("test", "fromPhone");
        chatText.setText(getIntent().getStringExtra("New Text"));
        _cli.set_chatLine(getIntent().getStringExtra("New Text"));

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_MSG, getIntent().getStringExtra("New Text"));
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_CHATID, getIntent().getStringExtra("ChatNum"));
        long newRowId = chatdb.insertOrThrow(_DBtask, null, values);
        if (newRowId == -1) {
            Toast.makeText(getApplicationContext(), "Oh no", Toast.LENGTH_SHORT).show();
        }
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
        if (_canUpdate)
            update();
        /*if(_canUpdate) {
            setChatLinesFromPhone();
        }*/
        //so ChorusChat doesn't open everytime a new message is posted
    }

    /*
    Recursive function that constantly checks the server to see if there is a change in the chat.
     */
    public void update() {
        if (c.moveToFirst()) {
            Log.i("test", "update");
            ChatLineInfo cli = new ChatLineInfo();
            String msg = c.getString(c.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry
                    .COLUMN_NAME_MSG));
            cli.set_chatLine(msg);
            String id = c.getString(c.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry
                    .COLUMN_NAME_CHATID));
            cli.set_id(id);
            c.moveToNext();
            chatText.setText(cli.get_chatLine());

            if (msg.contains("?")) {
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                        R.array.question_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            } else {
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                        R.array.response_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            }
        }
        //setChatLinesFromPhone();
        /*if(_canUpdate)
            update();*/
    }

    /*
    Sends the string to the server to add chat list.
     */
    public void postData(String words) {
        chatText.setText(words);
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_MSG, words);
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_CHATID, _cli.get_id());
        long newRowId = chatdb.insertOrThrow(_DBtask, null, values);
        if (newRowId == -1) {
            Toast.makeText(getApplicationContext(), "Oh no", Toast.LENGTH_SHORT).show();
        }
        if (words.contains("?")) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                    R.array.question_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        } else {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                    R.array.response_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

    /*@Override
    protected void onStop() {
        Log.i("test", "onstop");
        super.onStop();
        _canUpdate = false;
        //insertToDB();
//        setAlarmManager();
    }*/
    public void onPause() {//stops the recursion.
        Log.i("test", "stop");
        super.onPause();
        _canUpdate = false;
        //setAlarmManager();
    }

    public void onResume() {
        super.onResume();
        stopAlarmManager();
    }
}