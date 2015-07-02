package com.example.sunnysummer5.chorusreceiveinput2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class OpenOnPhone extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Node mNode; // the connected device to send the message to
    GoogleApiClient mGoogleApiClient;
    private String HELLO_WORLD_WEAR_PATH;
    private boolean mResolvingError = false;
    byte[] message;
    boolean open_on_phone_animation;
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_on_phone);

        //Connect the GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        String caller = getIntent().getStringExtra("caller");
        if (caller.equals("MainActivity")) {
            HELLO_WORLD_WEAR_PATH = "/main-activity-on-phone";
            message = null;
            open_on_phone_animation = true;
        } else if (caller.equals("Microphone")) {
            HELLO_WORLD_WEAR_PATH = "/microphone-on-phone";
            message = null;
            open_on_phone_animation = true;
        } else if (caller.equals("Speech")) {
            HELLO_WORLD_WEAR_PATH = "/speech-on-phone";
            message = getIntent().getStringExtra("Words").getBytes();
            open_on_phone_animation = false;
        } else if (caller.equals("Response")) {
            HELLO_WORLD_WEAR_PATH = "/response";
            message = getIntent().getStringExtra("Response").getBytes();
            open_on_phone_animation = false;
        }

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                if (open_on_phone_animation) {
                    mTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendMessage();
                        }
                    });
                } else {
                    sendMessage();
                }
            }
        });
    }

    /**
     * Send message to mobile handheld
     */
    private void sendMessage() {
        if (mNode != null && mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Wearable.MessageApi.sendMessage(
                    mGoogleApiClient, mNode.getId(), HELLO_WORLD_WEAR_PATH, message).setResultCallback(

                    new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {

                            if (!sendMessageResult.getStatus().isSuccess()) {
                                Log.e("TAG", "Failed to send message with status code: "
                                        + sendMessageResult.getStatus().getStatusCode());
                            } else {
                                if (open_on_phone_animation) {
                                    Intent intent = new Intent(getApplicationContext(), ConfirmationActivity.class);
                                    intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                                            ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
                                    intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Opening on Phone");
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(getApplicationContext(), ConfirmationActivity.class);
                                    intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                                            ConfirmationActivity.SUCCESS_ANIMATION);
                                    intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Message Sent");
                                    startActivity(intent);
                                }
                            }
                        }
                    }
            );
        } else {
            Intent intent = new Intent(getApplicationContext(), ConfirmationActivity.class);
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                    ConfirmationActivity.FAILURE_ANIMATION);
            intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Message Send Failure");
            startActivity(intent);
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
    }
}