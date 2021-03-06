package guillermobeltran.chorusinput;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import guillermobeltran.chorusinput.PushService.ParseUtils;

import static guillermobeltran.chorusinput.R.id.SendButton;
import static guillermobeltran.chorusinput.R.id.imageButton;

/*
Activity for when user is about to ask a question. Camera for another time.
TODO: Change chatNum and task number to specific to the user.
 */
public class SpeakToMe extends Activity {

    private EditText _txtSpeechInput;
    private Bitmap _finalBm;
    private ImageButton _btnSpeak, _btnCam;
    private Button _sendButton;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private static final int REQ_CODE_CAMERA_IMAGE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speak_to_me);

        _txtSpeechInput = (EditText) findViewById(R.id.txtSpeechInput);
        _btnSpeak = (ImageButton) findViewById(imageButton);
//        _btnCam = (ImageButton) findViewById(imageButton2); //btnCam not used.
        _sendButton = (Button) findViewById(SendButton);

        _btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
    }

    /*
    This is called by imagebutton@ in the xml file. Uses onClick.
     */
    public void takePic(View view) {
        Intent intent = new Intent(getApplicationContext(), TakePicture.class);
        startActivityForResult(intent, REQ_CODE_CAMERA_IMAGE);
    }

    /**
     * Showing google speech input dialog for STT
     */
    public void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    //ChatNum must be changed. And task number.
    public void sendButton(View v) {
        if(_txtSpeechInput.getText().toString().length()!=0) {
            Intent intent = new Intent(getApplicationContext(), ChorusChat.class);
            intent.putExtra("Words", _txtSpeechInput.getText().toString());
            intent.putExtra("Asking", true);
            intent.putExtra("Speech", false);
            intent.putExtra("Yelp", false);
            intent.putExtra("ChatNum", "6");
            intent.putExtra("Role", "requester");

            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("email", ParseUtils.customIdBuilder("6"));
            params.put("role", "requester");
            params.put("task", "6");
            params.put("message", _txtSpeechInput.getText().toString());
            ParseCloud.callFunctionInBackground("sendPushToUser", params, new FunctionCallback<String>() {
                public void done(String success, ParseException e) {
                    if (e == null) {
                        Log.e("ChorusChat", "Push sent successfully.");
                    }
                }
            });

            startActivity(intent);
        }
        else{
            Toast.makeText(this,"Can't have empty input!",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input or camera input. Camera code shouldn't be used.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && data != null) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    _txtSpeechInput.setText(result.get(0));//setting the text to what we said
                    if (result.get(0) != null) {
                        _sendButton.setVisibility(View.VISIBLE);
                    }
                }
                break;
            }
            //No need for this.
            case REQ_CODE_CAMERA_IMAGE: {
                if (resultCode == RESULT_OK) {
                    //retrieving the file from URI
                    String stringuri = data.getStringExtra("FILE_URI");
                    Uri uri = Uri.parse(stringuri);
                    File file = new File(uri.getPath());
                    //creating a bitmap from selected file
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap scaledbm = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
                    scaledbm = Bitmap.createScaledBitmap(scaledbm, _btnCam.getWidth(), _btnCam.getHeight(), true);
                    _finalBm = Bitmap.createBitmap(scaledbm, 0, 0, scaledbm.getWidth(), scaledbm.getHeight(), null, true);
                    //set camera button to that of bitmap and deletes file
                    _btnCam.setImageBitmap(_finalBm);
                    _btnCam.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    file.delete();
                    //for the user to rotate image because images can be displayed incorrectly\

                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Operation canceled\n", Toast.LENGTH_LONG).show();
                } else if (resultCode == 100) {
                    takePic(_btnCam.getRootView());
                }
                break;
            }
        }
    }
}