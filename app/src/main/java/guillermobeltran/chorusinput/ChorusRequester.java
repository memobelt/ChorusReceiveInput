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
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.AbstractAQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static guillermobeltran.chorusinput.R.id.ChorusRequesterPage;

public class ChorusRequester extends Activity {
    Intent _intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chorus_requester);
        WebView wb = (WebView) findViewById(ChorusRequesterPage);
        WebSettings webSettings = wb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            wb.loadUrl("http://128.237.179.10:8888/requester.php?task=6");
            if(getIntent().getExtras()!=null) {
                _intent = getIntent();
                if (_intent.getExtras().getBoolean("Asking")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                postData();
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
        String url = "http://128.237.179.10:8888/php/chatProcess.php";
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
        Intent intent = getIntent();
        String words = intent.getStringExtra("Words");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("action", "post");
        params.put("role", "requester");
        params.put("task", "6");
        params.put("workerId", "cb3c5a38b4999401ec88a7f8bf6bd90f");
        params.put("chatLine", words);

        AQuery aq = new AQuery(this);
        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
//                json.toString();
            }
        });
    }
}
