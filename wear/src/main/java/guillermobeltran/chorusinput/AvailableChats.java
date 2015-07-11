package guillermobeltran.chorusinput;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

public class AvailableChats extends FragmentActivity {

    private TextView mTextView;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_chats);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                if(findViewById(R.id.fragment_container) != null) {
                    if(savedInstanceState != null) {
                        return;
                    }
                    AvailableChatsFragment fragment = new AvailableChatsFragment();
                    fragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragment_container, fragment).commit();
                }
            }
        });
    }
}
