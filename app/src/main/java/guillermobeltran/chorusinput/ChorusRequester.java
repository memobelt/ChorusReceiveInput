package guillermobeltran.chorusinput;

import android.app.Activity;
import android.content.Context;
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

import static guillermobeltran.chorusinput.R.id.ChorusWebpage;
import static guillermobeltran.chorusinput.R.id.button1;

public class ChorusRequester extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chorus_requester);


    }
    public void getConnection(View v){
        WebView wb = (WebView) findViewById(ChorusWebpage);
        WebSettings webSettings = wb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        Button button = (Button) findViewById(button1);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            button.setVisibility(View.GONE);
            wb.setVisibility(View.VISIBLE);
            wb.loadUrl("http://128.237.179.10:8888/requester.php?task=1");
        } else {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }
}
