package guillermobeltran.chorusinput;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Memo on 6/18/15 to update in the back ground. Probably not useful. Push system
 * will be much easier on battery. Is not functioning atm.
 */
public class AlarmUpdateChatList extends BroadcastReceiver {
    int _size, numNotifications = 0;
    String _task,_role;
    ChorusChat chat = new ChorusChat();
    @Override
    public void onReceive(final Context context, Intent intent) {
        context.startService(new Intent(context,UpdateService.class));
    }

}
