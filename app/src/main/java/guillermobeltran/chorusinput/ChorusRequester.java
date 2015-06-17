package guillermobeltran.chorusinput;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.Map;

import static guillermobeltran.chorusinput.R.id.RequesterList;

public class ChorusRequester extends Activity {
    Intent _intent;
    ListView _requesterList;
    ArrayList<ChatLineInfo> chatLineInfoArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chorus_requester);
        _requesterList = (ListView) findViewById(RequesterList);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            chatLineInfoArrayList = new ArrayList<ChatLineInfo>();
            if(getIntent().getExtras()!=null) {
                _intent = getIntent();
                if (_intent.getExtras().getBoolean("Asking")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                postData();
                                setChat();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        } else {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }
    public void postData() {
        //To be changed for actual website
        /*
        *  url: "php/chatProcess.php",
        type: "POST",
        async: false,
        data: {
        	action: "post",
            role: "requester",
            task: 6,
            workerId: "cb3c5a38b4999401ec88a7f8bf6bd90f",
            chatLine: "OMG"
        },
        * */
        String url = "http://128.237.179.10:8888/php/chatProcess.php";
        Intent intent = getIntent();
        String words = intent.getStringExtra("Words");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("action", "post");
        params.put("role", "requester");
        params.put("task", "6");
        params.put("workerId", "cb3c5a38b4999401ec88a7f8bf6bd90f");
        params.put("chatLine", "YO");

        AQuery aq = new AQuery(this);
        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                this.toString();
//                json.toString();
            }
        });
    }
    public void setChat(){
        /*

	$.ajax({
        url: "php/chatProcess.php",
        type: "POST",
        data: {
        	action: "fetchNewChatRequester",
        	role: this.role,
            task: this.mturk.task,
            workerId: workerId,
            lastChatId: lastChatId
        },
         */
        String url = "http://128.237.179.10:8888/php/chatProcess.php";
        Intent intent = getIntent();
        String words = intent.getStringExtra("Words");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("action", "fetchNewChatRequester");
        params.put("role", "requester");
        params.put("task", "6");
        params.put("workerId", "cb3c5a38b4999401ec88a7f8bf6bd90f");
        params.put("lastChatId", "-1");

        AQuery aq = new AQuery(this);
        aq.ajax(url, params, JSONArray.class, new AjaxCallback<JSONArray>() {

            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                if (json!=null){
                    try {
                        ArrayList<String> arrayList = new ArrayList<String>();
                        for (int n = 0; n < json.length();n++){
                            String[] lineInfo = json.get(n).toString().split("\"");
                            ChatLineInfo chatLineInfo = new ChatLineInfo();
                            for(int i = 1; i < lineInfo.length; i+=4) {
                                switch (lineInfo[i]) {
                                    case "id":
                                        chatLineInfo.set_id(lineInfo[i + 2]);
                                        break;
                                    case "chatLine":
                                        chatLineInfo.set_chatLine(lineInfo[i + 2]);
                                        break;
                                    case "role":
                                        chatLineInfo.set_role(lineInfo[i + 2]);
                                        break;
                                    case "task":
                                        chatLineInfo.set_task(lineInfo[i + 2]);
                                        break;
                                    case "time":
                                        chatLineInfo.set_time(lineInfo[i + 2]);
                                        break;
                                    case "accepted":
                                        chatLineInfo.set_accepted(lineInfo[i + 2]);
                                        break;
                                    case "workerId":
                                        chatLineInfo.set_workerId(lineInfo[i + 2]);
                                        break;
                                    case "acceptedTime":
                                        chatLineInfo.set_acceptedTime(lineInfo[i + 2]);
                                        break;
                                }
                            }
                            chatLineInfoArrayList.add(chatLineInfo);
                            arrayList.add(chatLineInfo.get_role()+" : " + chatLineInfo.get_chatLine());
                        }
                        ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(),
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
                        ((AdapterView<ListAdapter>) _requesterList).setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}
