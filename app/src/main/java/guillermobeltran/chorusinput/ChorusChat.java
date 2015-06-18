package guillermobeltran.chorusinput;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static guillermobeltran.chorusinput.R.id.CrowdList;
import static guillermobeltran.chorusinput.R.id.editText;


public class ChorusChat extends Activity {
    EditText _editText;
    String _task;
    ListView _crowdList;
    ArrayList<ChatLineInfo> chatLineInfoArrayList;
    ArrayList<String> arrayList = new ArrayList<String>();
    ChatLineInfo cli = new ChatLineInfo();
    ArrayAdapter adapter;
    Handler _handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chorus_chat);
        _crowdList = (ListView) findViewById(CrowdList);
        _editText = (EditText) findViewById(editText);
        _task = getIntent().getStringExtra("ChatNum");
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            chatLineInfoArrayList = new ArrayList<ChatLineInfo>();
            adapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1, arrayList){
                @Override
                public View getView(int position, View convertView,
                                    ViewGroup parent) {
                    View view =super.getView(position, convertView, parent);

                    TextView textView=(TextView) view.findViewById(android.R.id.text1);

                            /*YOUR CHOICE OF COLOR*/
                    textView.setTextColor(Color.BLACK);

                    return view;
                }
            };
            cli.setChat(this, "crowd", _task, chatLineInfoArrayList, arrayList, adapter, _crowdList);
            _handler = new Handler();

        } else {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendText(View v){
        cli.postData(_editText.getText().toString(),_task,"crowd",this);
        _editText.setText("");
        Map<String, Object> params = cli.setUpParams(new HashMap<String, Object>(),
                "fetchNewChatRequester","crowd",_task);
        new Thread(new UpdateChatList(cli, chatLineInfoArrayList,adapter,_crowdList, params,this))
                .start();
    }
}
