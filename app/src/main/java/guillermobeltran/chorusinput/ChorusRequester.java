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
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import org.w3c.dom.Document;

import static guillermobeltran.chorusinput.R.id.ChorusWebpage;
import static guillermobeltran.chorusinput.R.id.button1;

public class ChorusRequester extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chorus_requester);
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
            wb.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
//                    view.loadUrl("javascript:document.getElementById('txt').[0].innerHTML = 'any text';");
                }
            });
            wb.loadUrl("http://128.237.179.10:8888/requester.php?task=3");
//            wb.loadUrl("javascript:js/Class/Chat.requester.js('This works')");
//            wb.loadUrl("javascript:js/Class/Chat.requester.js(Not working)");
//            wb.loadUrl("javascript:js/Class/Chat.requester.postChatRequester(ASDsdgfF )");
//            wb.loadUrl("javascript:js/Class/Chat.requester.postChatRequester('qwer')");
        } else {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }
}
