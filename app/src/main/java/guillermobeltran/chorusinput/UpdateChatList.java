package guillermobeltran.chorusinput;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Memo on 6/18/15.
 */
public class UpdateChatList implements Runnable {
    ArrayList<ChatLineInfo> _chatLineInfoArrayList;
    ChatLineInfo _cli;
    ArrayAdapter _adapter;
    ListView _listView;
    Map<String,Object> _params;
    Activity _act;
    public UpdateChatList(ChatLineInfo cli, ArrayList<ChatLineInfo> cliArray, ArrayAdapter aa,
                          ListView lv, Map<String,Object> pa, Activity act){
        _cli = cli;
        _chatLineInfoArrayList = cliArray;
        _adapter = aa;
        _listView = lv;
        _params = pa;
        _act = act;
        ChorusRequester _a = (ChorusRequester) act;
        _a.teset(_a.arrayList.get(0));
    }

    @Override
    public void run() {
        String url = "http://128.237.179.10:8888/php/chatProcess.php";
        AQuery aq = new AQuery(_act);
        aq.ajax(url, _params, JSONArray.class, new AjaxCallback<JSONArray>() {

            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                if (json != null) {
                    if (json.length()>_chatLineInfoArrayList.size()){
                        try {
                            String[] lineInfo = json.get(json.length()-1).toString().split("\"");
                            ChatLineInfo chatLineInfo = _cli.getChatLineInfo(lineInfo,new ChatLineInfo());
                            _chatLineInfoArrayList.add(chatLineInfo);
                            _adapter.add(chatLineInfo.get_role() + " : " + chatLineInfo.get_chatLine());
                            _listView.setSelection(_listView.getCount()-1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}
