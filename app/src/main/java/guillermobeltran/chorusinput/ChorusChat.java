package guillermobeltran.chorusinput;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static guillermobeltran.chorusinput.R.id.CrowdList;
import static guillermobeltran.chorusinput.R.id.editText;


public class ChorusChat extends Activity {
    EditText _editText;
    String _task;
    ListView _crowdList;
    ArrayList<ChatLineInfo> chatLineInfoArrayList;
    ChatLineInfo cli = new ChatLineInfo();

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
            cli.setChat("crowd",this,_task,chatLineInfoArrayList,_crowdList,getApplicationContext());
        } else {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendText(View v){
        cli.postData(_editText.getText().toString(),_task,"crowd",this);
        _editText.setText("");
    }
}
