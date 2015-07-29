package guillermobeltran.chorusinput;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.nio.charset.Charset;
/*
Created by Summer.
 */
public class OpenOnWatch extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Node mNode; // the connected device to send the message to
    GoogleApiClient mGoogleApiClient;
    private static String HELLO_WORLD;
    private boolean mResolvingError = false;
    String message = "";

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

        if(getIntent().getExtras().getBoolean("Text")) {
            //post message on Chorus Chat (new text or current database chatline)
            HELLO_WORLD = "/hello-world";
            message = getIntent().getStringExtra("Role")+ "|" + getIntent().getStringExtra("Message")
                    + "+=+" + getIntent().getStringExtra("Time") + "~" + getIntent().getStringExtra("ChatNum")
                    + "#|#" + getIntent().getStringExtra("ID");
            if(getIntent().getExtras().getBoolean("Answer")) {
                //post current database chatline so crowd can answer from watch
                message = "?" + message;
            }
        }
        else {
            //caller from notification. open Chorus Chat on watch.
            HELLO_WORLD = "/hello-world-open";
            message = "";
        }
    }

    /**
     * Send message to mobile handheld
     */
    private void sendMessage() {
        if (mNode != null && mGoogleApiClient != null && mGoogleApiClient.isConnected()) {

            Wearable.MessageApi.sendMessage(
                    mGoogleApiClient, mNode.getId(), HELLO_WORLD,
                            message.getBytes(Charset.forName("UTF-8"))).setResultCallback(
                    new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            if (!sendMessageResult.getStatus().isSuccess()) {
                                Log.e("TAG", "Failed to send message with status code: "
                                        + sendMessageResult.getStatus().getStatusCode());
                            } else {
                                //Log.i("test", "Message sent");
                            }
                        }
                    }
            );
        } else {
            //Log.i("test", "Not connected");
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
                }
                sendMessage();
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
        finish();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("test", "Connection failed");
        finish();
    }
}