package guillermobeltran.chorusinput;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import static guillermobeltran.chorusinput.R.id.ChorusChatPage;
import static guillermobeltran.chorusinput.R.id.button2;


public class ChorusChat extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chorus_chat);
    }
    public void getChatConnected(View V){
        WebView wb = (WebView) findViewById(ChorusChatPage);
        WebSettings webSettings = wb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        Button button = (Button) findViewById(button2);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            button.setVisibility(View.GONE);
            wb.setVisibility(View.VISIBLE);
            Intent intent = getIntent();
            String url = "http://128.237.179.10:8888/chat.php?task=";
            wb.loadUrl(url+intent.getStringExtra("ChatNum"));
        } else {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }
}
