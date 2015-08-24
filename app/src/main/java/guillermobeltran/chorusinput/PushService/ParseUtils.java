package guillermobeltran.chorusinput.PushService;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;


/**
 * Created by Jason on 7/16/15.
 */
public class ParseUtils {

    private static String TAG = ParseUtils.class.getSimpleName();

    private static String CHORUS_MAIL_SUFFIX = "@chorus.com";

    private static String CHORUS_TASK = "task_";


    /**
     * Register parse with two api keys
     * @param context
     */
    public static void registerParse(Context context) {
        // initializing parse library
        Parse.initialize(context, ParseConfig.PARSE_APPLICATION_ID, ParseConfig.PARSE_CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParsePush.subscribeInBackground(ParseConfig.PARSE_CHANNEL, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e(TAG, "Successfully subscribed to Parse!");
            }
        });
    }

    /**
     * Subscribe to account (already established using my email:jeisen8383@gmail.com
     * @param email
     */
    public static void subscribeWithEmail(String email) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();

        installation.put("email", email);

        installation.saveInBackground();

        Log.e(TAG, "Subscribed with email: " + email);

    }

    public static String customIdBuilder(String taskId) {
        return CHORUS_TASK + taskId + CHORUS_MAIL_SUFFIX;
    }
}
