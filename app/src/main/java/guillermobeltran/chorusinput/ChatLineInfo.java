package guillermobeltran.chorusinput;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Memo on 6/17/15.
 */
public class ChatLineInfo {
    private String _id, _chatLine,_role,_task,_time,_accepted,_workerId, _acceptedTime;
    public ChatLineInfo(){

    }

    public void set_id(String id) {
        _id = id;
    }

    public String get_id() {
        return _id;
    }

    public void set_chatLine(String chatLine) {
        _chatLine = chatLine;
    }

    public String get_chatLine() {
        return _chatLine;
    }

    public void set_role(String role) {
        _role = role;
    }

    public String get_role() {
        return _role;
    }

    public void set_task(String task) {
        _task = task;
    }

    public String get_task() {
        return _task;
    }

    public void set_time(String time) {
        _time = time;
    }

    public String get_time() {
        return _time;
    }

    public void set_accepted(String accepted) {
        _accepted = accepted;
    }

    public String get_accepted() {
        return _accepted;
    }

    public void set_workerId(String workerId) {
        _workerId = workerId;
    }

    public String get_workerId() {
        return _workerId;
    }

    public void set_acceptedTime(String acceptedTime) {
        _acceptedTime = acceptedTime;
    }

    public String get_acceptedTime() {
        return _acceptedTime;
    }

    public void postData(String words, String task, String role, Activity activity) {
        String url = "http://128.237.179.10:8888/php/chatProcess.php";
        Map<String, Object> params = setUpParams(new HashMap<String, Object>(),"post",
                role,task);
        params.put("chatLine", words);
        AQuery aq = new AQuery(activity);
        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
            }
        });
    }

    public void setChat(Activity activity, String role, String task,
                        final ArrayList<ChatLineInfo> chat_line_list, final ArrayList<String> arrayList,
                        final ArrayAdapter adapter, final ListView list_view) {
        String url = "http://128.237.179.10:8888/php/chatProcess.php";
        Map<String, Object> params = setUpParams(new HashMap<String, Object>(),"fetchNewChatRequester",
                role,task);

        AQuery aq = new AQuery(activity);
        aq.ajax(url, params, JSONArray.class, new AjaxCallback<JSONArray>() {

            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                if (json!=null){
                    try {
                        for (int n = 0; n < json.length();n++){
                            String[] lineInfo = json.get(n).toString().split("\"");
                            ChatLineInfo chatLineInfo = getChatLineInfo(lineInfo, new ChatLineInfo());
                            chat_line_list.add(chatLineInfo);
                            arrayList.add(chatLineInfo.get_role()+" : " + chatLineInfo.get_chatLine());
                        }
                        ((AdapterView<ListAdapter>) list_view).setAdapter(adapter);
                        list_view.setSelection(list_view.getCount()-1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    public ChatLineInfo getChatLineInfo(String[] lineInfo, ChatLineInfo chatLineInfo){
        for(int i = 1; i < lineInfo.length; i+=4){
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
        return chatLineInfo;
    }

    public Map<String,Object> setUpParams(HashMap<String, Object> params, String action, String role, String task){
        params.put("action", action);
        params.put("role", role);
        params.put("task", task);
        params.put("workerId", "cb3c5a38b4999401ec88a7f8bf6bd90f");
        params.put("lastChatId", "-1");
        return params;
    }
}
