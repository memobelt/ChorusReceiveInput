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
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static guillermobeltran.chorusinput.R.id.RequesterList;
import static guillermobeltran.chorusinput.R.id.editText1;

public class ChorusRequester extends Activity {
    Intent _intent;
    ListView _requesterList;
    EditText _editText;
    ArrayList<ChatLineInfo> chatLineInfoArrayList;
    ArrayList<String> arrayList = new ArrayList<String>();
    ChatLineInfo cli = new ChatLineInfo();
    ArrayAdapter adapter;
    Handler _handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chorus_requester);
        _requesterList = (ListView) findViewById(RequesterList);
        _editText = (EditText) findViewById(editText1);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            chatLineInfoArrayList = new ArrayList<ChatLineInfo>();
            if(getIntent().getExtras()!=null) {
                _intent = getIntent();
                if (_intent.getExtras().getBoolean("Asking")) {
                    String words = _intent.getStringExtra("Words");
                    cli.postData(words,"6","requester",this);
                }
            }
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
            cli.setChat(this, "requester","6", chatLineInfoArrayList, arrayList, adapter, _requesterList);
            _handler = new Handler();
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent broadcast_intent = new Intent(this, AlarmUpdateChatList.class);
            broadcast_intent.putExtra("ArrayList", chatLineInfoArrayList.size());
//            broadcast_intent.putExtra("")
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, broadcast_intent, 0);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(), 1000, pendingIntent);
        } else {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    public void requesterSend(View v){
        cli.postData(_editText.getText().toString(), "6", "requester", this);
        _editText.setText("");
        update();
    }
    public void update(){
        Map<String, Object> params = cli.setUpParams(new HashMap<String, Object>(),
                "fetchNewChatRequester","requester","6");
        new Thread(new UpdateChatList(cli, chatLineInfoArrayList,adapter,_requesterList, params,this))
                .start();
    }
    public void teset(String s){
        Toast.makeText(this,"WOW",Toast.LENGTH_SHORT).show();
    }
}