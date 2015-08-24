package guillermobeltran.chorusinput.PushService;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import java.util.List;

import guillermobeltran.chorusinput.OpenOnWatch;
import guillermobeltran.chorusinput.R;


/**
 * Created by Jason on 7/16/15.
 */
public class NotificationUtils {

    private String TAG = NotificationUtils.class.getSimpleName();

    private Context mContext;

    private int notificationID;

    public NotificationUtils() {
        notificationID = 001;
    }

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void showNotificationMessage(String title, String message, Intent intent) {

        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        Intent wear = new Intent(mContext, OpenOnWatch.class);
        wear.putExtra("Text", false);
        PendingIntent wearPI = PendingIntent.getActivity(mContext, 0, wear, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.abc_ic_menu_share_mtrl_alpha,
                        "Open", wearPI).build();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title).setAutoCancel(true)
                .setWhen(System.currentTimeMillis()).setContentIntent(resultPendingIntent)
                .extend(new NotificationCompat.WearableExtender().addAction(action));

        mBuilder.setContentText(message);
        NotificationManager nmgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        nmgr.notify(notificationID++, mBuilder.build());

    }

    /**
     * Method checks if the app is in background or not (Future use)
     *
     * @param context
     * @return
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
}
