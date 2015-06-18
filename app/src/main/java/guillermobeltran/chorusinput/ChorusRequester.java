package guillermobeltran.chorusinput;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
        _requesterList = (ListView) findViewById(RequesterList);
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
            arrayList = new ArrayList<String>();
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
        } else {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
//        AlarmManager alarm = (AlarmManager) getApplicationContext()
//                .getSystemService(Context.ALARM_SERVICE);
//        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10000, pendingIntent);;

    }

    public void requesterSend(View v){
        cli.postData(_editText.getText().toString(), "6", "requester", this);
        _editText.setText("");
        new Thread(new update()).start();
    }
    class update implements Runnable{

        @Override
        public void run() {
            String url = "http://128.237.179.10:8888/php/chatProcess.php";
            Map<String, Object> params = cli.setUpParams(new HashMap<String, Object>(),
                    "fetchNewChatRequester","requester","6");

            AQuery aq = new AQuery(getApplicationContext());
            aq.ajax(url, params, JSONArray.class, new AjaxCallback<JSONArray>() {

                @Override
                public void callback(String url, JSONArray json, AjaxStatus status) {
                    if (json != null) {
                        if (json.length()>chatLineInfoArrayList.size()){
                            try {
                                String[] lineInfo = json.get(json.length()-1).toString().split("\"");
                                ChatLineInfo chatLineInfo = cli.getChatLineInfo(lineInfo,new ChatLineInfo());
                                chatLineInfoArrayList.add(chatLineInfo);
                                adapter.add(chatLineInfo.get_role() + " : " + chatLineInfo.get_chatLine());
                                _requesterList.setSelection(_requesterList.getCount()-1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    }
}