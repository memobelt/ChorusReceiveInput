package guillermobeltran.chorusinput;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class OpenOnWatch extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Node mNode; // the connected device to send the message to
    GoogleApiClient mGoogleApiClient;
    private static final String HELLO_WORLD = "/hello-world";
    private boolean mResolvingError=false;
    TextView t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_on_watch);

        //Connect the GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        t = (TextView) findViewById(R.id.text);
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }
    /**
     * Send message to mobile handheld
     */
    private void sendMessage() {
        if (mNode != null && mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            Wearable.MessageApi.sendMessage(
                    mGoogleApiClient, mNode.getId(), HELLO_WORLD, getIntent().getStringExtra("Message").getBytes())
                    .setResultCallback(
                            new ResultCallback<MessageApi.SendMessageResult>() {
                                @Override
                                public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                    if (!sendMessageResult.getStatus().isSuccess()) {
                                        Log.e("TAG", "Failed to send message with status code: "
                                                + sendMessageResult.getStatus().getStatusCode());
                                    } else {
                                        Log.i("test", "Message sent");
                                        Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_SHORT).show();
                                    }
                                }
                    }
            );
        }else{
            if(mNode==null) {
            Log.i("test", "Not connected"); }
//            Toast.makeText(getApplicationContext(), "Not connected", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    /*
     * Resolve the node = the connected device to send the message to
     */
    private void resolveNode() {
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi
                .GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                for (Node node : nodes.getNodes()) {
                    mNode = node;
                    Log.i("test", mNode.getDisplayName());
                }
            }
        });
    }


    @Override
    public void onConnected(Bundle bundle) {
        resolveNode();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("test", "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("test", "Connection failed");
        finish();
    }
}