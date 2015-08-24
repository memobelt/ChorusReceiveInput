package guillermobeltran.chorusinput;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

import guillermobeltran.chorusinput.PushService.ParseUtils;

/**
 * Created by Jason on 7/16/15.
 */
public class ChorusApplication extends Application {

    private static ChorusApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        // register with parse
        ParseUtils.registerParse(this);

    }


    public static synchronized ChorusApplication getInstance() {
        return mInstance;
    }

}
