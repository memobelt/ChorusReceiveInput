package guillermobeltran.chorusinput;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import static guillermobeltran.chorusinput.R.id.SendButton;
import static guillermobeltran.chorusinput.R.id.imageButton;
import static guillermobeltran.chorusinput.R.id.imageButton2;


public class SpeakToMe extends Activity {

    private TextView _txtSpeechInput;
    private Bitmap _finalBm;
    private ImageButton _btnSpeak, _btnCam;
    private Button _sendButton;
//    private Button _btnRig, _btnLef, _btnOk;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private static final int REQ_CODE_CAMERA_IMAGE = 200;
    private static final String TAG = "SpeakToMe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speak_to_me);

        _txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        _btnSpeak = (ImageButton) findViewById(imageButton);
        _btnCam = (ImageButton) findViewById(imageButton2);
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
    public void takePic(View view){
        Intent intent = new Intent(getApplicationContext(), TakePicture.class);
        startActivityForResult(intent, REQ_CODE_CAMERA_IMAGE);
    }

    /**
     * Showing google speech input dialog
     * */
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
    public void sendButton(View v){
        Intent intent = new Intent(getApplicationContext(),ChorusChat.class);
        intent.putExtra("Words", _txtSpeechInput.getText());
        intent.putExtra("Asking",true);
        intent.putExtra("ChatNum", "6");
        intent.putExtra("Role", "requester");
        startActivity(intent);
    }
    /**
     * Receiving speech input or camera input
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
                }else if (resultCode==100){
                    takePic(_btnCam.getRootView());
                }
                break;
            }
        }
    }
}