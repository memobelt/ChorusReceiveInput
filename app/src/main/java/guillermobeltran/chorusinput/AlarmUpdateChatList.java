package guillermobeltran.chorusinput;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Memo on 6/18/15.
 */
public class AlarmUpdateChatList extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        String url = "http://128.237.179.10:8888/php/chatProcess.php";
        AQuery aq = new AQuery(context);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("action", "fetchNewChatRequester");
        params.put("role", "crowd");
        params.put("task", "3");
        params.put("workerId", "cb3c5a38b4999401ec88a7f8bf6bd90f");
        params.put("lastChatId", "-1");
        final int size = intent.getExtras().getInt("ArrayList");

        aq.ajax(url, params, JSONArray.class, new AjaxCallback<JSONArray>() {
            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                if (json != null) {
                    if (json.length()>size){
                        Toast.makeText(context,"HAHA",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
