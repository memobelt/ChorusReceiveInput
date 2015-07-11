package guillermobeltran.chorusinput;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


public class Microphone extends Activity {

    private TextView _txtSpeechInput;
    private TextView mTextView;
    private ImageButton _btnSpeak, _btnCam;
    private Button _sendButton;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private static final int REQ_CODE_CAMERA_IMAGE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microphone);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                _txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
                _btnSpeak = (ImageButton) findViewById(R.id.micButton);
                _btnCam = (ImageButton) findViewById(R.id.imageButton2);
                _sendButton = (Button) findViewById(R.id.SendButton);
                //microphone button
                _btnSpeak.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        promptSpeechInput();
                    }
                });
                //send button goes to ChorusChat
                _sendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),OpenOnPhone.class);
                        intent.putExtra("caller", "Speech");
                        intent.putExtra("Words", _txtSpeechInput.getText());
                        intent.putExtra("Asking",true);
                        intent.putExtra("ChatNum", "6");
                        intent.putExtra("Role", "requester");
                        startActivity(intent);
                    }
                });
                _btnCam.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), OpenOnPhone.class);
                        intent.putExtra("caller", "Microphone");
                        startActivity(intent);
                    }
                });
            }
        });
    }/*
    /**
     * Showing google speech input dialog
     * */
    public void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Ask Chorus");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),"Sorry! Your device doesn't support speech input",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    _txtSpeechInput.setText(result.get(0));//setting the text to what we said
                    if(result.get(0)!=null){
                        _sendButton.setVisibility(View.VISIBLE);
                    }
                }
                break;
            }
        }
    }
}