package guillermobeltran.speechrecognition;

import android.content.Context;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;


public class SpeakToMe extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speak_to_me);
        final Button button = (Button) findViewById(R.id.button);
        final Button button2 = (Button) findViewById(R.id.button2);
        final SpeechRecognizer speech = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Context context = getApplicationContext();
                CharSequence passed = "Speech Recognition passed";
                CharSequence failed = "Speech Recognition failed";
                int duration = Toast.LENGTH_SHORT;
                if(SpeechRecognizer.isRecognitionAvailable(context)){
                    Toast toast = Toast.makeText(context, passed, duration);
                    toast.show();
                    Listen(context, speech);
                }
                else{
                    Toast toast = Toast.makeText(context, failed, duration);
                    toast.show();
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                speech.stopListening();
            }
        });
    }

    public void Listen(Context context, SpeechRecognizer speech){
        Intent rec = RecognizerIntent.getVoiceDetailsIntent(context);
        RecognitionListener listener = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
            ArrayList<String> voiceResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            StringBuilder sb = new StringBuilder();
                for(String p: voiceResults){
                    sb.append(p);
                    sb.append("\n");
                }
                Toast te = new Toast(getApplicationContext());
                te.setText(sb.toString());
                te.setDuration(Toast.LENGTH_SHORT);
                te.show();
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        };
        speech.setRecognitionListener(listener);
        Intent recognizerIntent = RecognizerIntent.getVoiceDetailsIntent(getApplicationContext());
        speech.startListening(recognizerIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
