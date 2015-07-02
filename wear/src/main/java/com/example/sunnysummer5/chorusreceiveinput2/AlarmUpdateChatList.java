package com.example.sunnysummer5.chorusreceiveinput2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmUpdateChatList extends BroadcastReceiver {
    int _size, numNotifications = 0;
    String _task,_role;
    ChorusChat chat = new ChorusChat();
    @Override
    public void onReceive(final Context context, Intent intent) {
        context.startService(new Intent(context,UpdateService.class));
    }

}