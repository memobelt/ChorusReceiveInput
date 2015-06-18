package guillermobeltran.chorusinput;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chorus_chat);
        _crowdList = (ListView) findViewById(CrowdList);
        _editText = (EditText) findViewById(editText);
        chatLineInfoArrayList = new ArrayList<ChatLineInfo>();
        _task = getIntent().getStringExtra("ChatNum");
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String url = "http://128.237.179.10:8888/chat.php?task="+ _task;
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

        } else {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendText(View v){
        cli.postData(_editText.getText().toString(),_task,"crowd",this);
        _editText.setText("");
    }
}
