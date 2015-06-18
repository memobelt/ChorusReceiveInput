package guillermobeltran.chorusinput;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static guillermobeltran.chorusinput.R.id.RequesterList;
import static guillermobeltran.chorusinput.R.id.editText1;

public class ChorusRequester extends Activity {
    Intent _intent;
    ListView _requesterList;
    EditText _editText;
    ArrayList<ChatLineInfo> chatLineInfoArrayList;
    ChatLineInfo cli = new ChatLineInfo();

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
            cli.setChat("requester",this,"6",chatLineInfoArrayList,_requesterList,
                    getApplicationContext());
        } else {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    public void requesterSend(View v){
        cli.postData(_editText.getText().toString(),"6","requester",this);
        _editText.setText("");
    }
}