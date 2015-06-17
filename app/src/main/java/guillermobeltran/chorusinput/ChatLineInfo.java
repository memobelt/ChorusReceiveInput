package guillermobeltran.chorusinput;

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
}
