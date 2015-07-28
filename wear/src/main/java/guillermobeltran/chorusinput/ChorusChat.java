package guillermobeltran.chorusinput;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


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
    DBHelper1 DbHelper;
    SQLiteDatabase chatdb;
    Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initializations
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chorus_chat);
        _chatLineInfoArrayList = new ArrayList<ChatLineInfo>();
        _task = getIntent().getStringExtra("ChatNum");
        //temporary for login
        if (_task == null) {
            Log.i("test", "null task 1");
            _task = "6";
        }
        _cli.set_task(getIntent().getStringExtra("ChatNum"));
        _canUpdate = true;
        _DBtask = "CHAT" + _task;
        DbHelper = new DBHelper1(getApplicationContext(), _DBtask);
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
                        } else if (parent.getItemAtPosition(position).equals("Reply...")) {
                            send.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(getApplicationContext(), "Select a response",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            send.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    postData(parent.getItemAtPosition(position).toString());
                                }
                            });
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getApplicationContext(), "Select a response",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                //update from phone
                if (getIntent().getStringExtra("caller").equals("ListenerServiceFromPhone")) {
                    setChatLinesFromPhone();
                }
                //Notification "Open" action
                else if (getIntent().getStringExtra("caller").equals("Open")) {
                    Log.i("test", "here1");
                    update();
                } else if (getIntent().getStringExtra("caller").equals("Open2")) {
                    _cli.set_task(getIntent().getStringExtra("ChatNum"));
                    _task = getIntent().getStringExtra("ChatNum");
                    Log.i("test", "here2");
                    update();

                }
                //speech input from Microphone class
                else if (getIntent().getStringExtra("caller").equals("Speech")) {
                    postData(getIntent().getStringExtra("Words"));
                } else {
                    if (c.getCount() > 0) {
                        //setChatLinesFromDB(c);
                        update();
                    }
                }
            }
        });

    }

    /*public void setChatLinesFromDB(Cursor c) {
        /*if (c.moveToFirst()) {
            ChatLineInfo cli = new ChatLineInfo();
            //while (!c.isAfterLast()) {
            //while(c.moveToNext()) {
            c.moveToLast();
            String role = c.getString(c.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry
                    .COLUMN_NAME_ROLE1));
            cli.set_role(role);
            _cli.set_role(role);
            String msg = c.getString(c.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry
                    .COLUMN_NAME_MSG));
            cli.set_chatLine(msg);
            _cli.set_chatLine(msg);
            chatText.setText(msg);
            String id = c.getString(c.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry
                    .COLUMN_NAME_CHATID));
            cli.set_id(id);
            _cli.set_id(id);
            c.moveToNext();
        }*/
        /*c.moveToLast();
        ChatLineInfo cli = new ChatLineInfo();
        String role = c.getString(c.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry
                .COLUMN_NAME_ROLE1));
        cli.set_role(role);
        _cli.set_role(role);
        String msg = c.getString(c.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry
                .COLUMN_NAME_MSG));
        cli.set_chatLine(msg);
        _cli.set_chatLine(msg);
        String id = c.getString(c.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry
                .COLUMN_NAME_CHATID));
        cli.set_id(id);
        _cli.set_id(id);
        String time = c.getString(c.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry
                .COLUMN_NAME_TIME));
        if(role.equals("requester")) {
            cli.set_acceptedTime(time);
            _cli.set_acceptedTime(time);
        }
        else {
            cli.set_time(time);
            _cli.set_time(time);
        }
        chatText.setText(cli.get_role() + " : " + cli.get_chatLine() + " " + getDate(time));

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
        update();
    }*/

    public void setChatLinesFromPhone() {
        _cli.set_role(getIntent().getStringExtra("Role"));
        _cli.set_chatLine(getIntent().getStringExtra("New Text"));
        _task = getIntent().getStringExtra("ChatNum");
        _cli.set_task(getIntent().getStringExtra("ChatNum"));

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_ROLE1, getIntent().getStringExtra("Role"));
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_MSG, getIntent().getStringExtra("New Text"));
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_CHATID, getIntent().getStringExtra("ChatNum"));
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_TIME, getIntent().getStringExtra("Time"));
        long newRowId = -1;
        try {
            newRowId = chatdb.insertOrThrow(_DBtask, null, values);
        } catch (SQLException e) {
            Log.e(chatdb.toString(), e.toString());
        }
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
        chatText.setText(getIntent().getStringExtra("Role") + " : " + getIntent().getStringExtra("New Text") +
                " " + getDate(getIntent().getStringExtra("Time")));
        if (getIntent().getExtras().getBoolean("Foreground")) {
            update();
        } else {
            finish();
        }
    }

    //sets chatLine from database
    public void update() {
        //update database and chatLineInfo
        c.moveToLast();
        ChatLineInfo cli = new ChatLineInfo();
        String role = c.getString(c.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry
                .COLUMN_NAME_ROLE1));
        cli.set_role(role);
        _cli.set_role(role);
        String msg = c.getString(c.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry
                .COLUMN_NAME_MSG));
        cli.set_chatLine(msg);
        _cli.set_chatLine(msg);
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

        chatText.setText(cli.get_role() + " : " + cli.get_chatLine() + " " + getDate(time));

        //change spinner if necessary
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
        /*if(_canUpdate) {
            update();
        }*/
    }

    /*
    Sends the string to the server to add chat list.
     */
    public void postData(String words) {
        _canUpdate = true;
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_ROLE1, "requester");
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_MSG, words);
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_CHATID, _cli.get_id());

        //time
        Date d = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-d H:mm:ss");
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_TIME, sdf.format(d));
        _cli.set_acceptedTime(sdf.format(d));

        long newRowId = chatdb.insertOrThrow(_DBtask, null, values);
        if (newRowId == -1) {
            Toast.makeText(getApplicationContext(), "Oh no", Toast.LENGTH_SHORT).show();
        }
        //change spinnner if necessary
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
        update();

        //send new message to phone
        Intent intent = new Intent(getApplicationContext(), OpenOnPhone.class);
        intent.putExtra("Response", words);
        //temporary for login
        if (_task == null) {
            Log.i("test", "null task 2");
            _task = "6";
        }
        intent.putExtra("ChatNum", _task);
        intent.putExtra("Time", sdf.format(d));
        intent.putExtra("caller", "Response");
        startActivity(intent);
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

    @Override
    protected void onStop() {
        super.onStop();
        _canUpdate = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        _canUpdate = false;
    }
}