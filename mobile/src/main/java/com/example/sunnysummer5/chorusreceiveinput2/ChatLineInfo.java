package com.example.sunnysummer5.chorusreceiveinput2;

import java.util.ArrayList;

/**
 * Created by Memo on 6/17/15. THis is what contains all the information about the line. May be needed
 * for future use?
 */
public class ChatLineInfo {
    private String _id, _chatLine,_role,_task,_time,_accepted,_workerId, _acceptedTime;
    private ArrayList<ChatLineInfo> chatLineInfoArrayList = new ArrayList<ChatLineInfo>();
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
    public ChatLineInfo setChatLineInfo(String[] lineInfo, ChatLineInfo chatLineInfo){
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
}
