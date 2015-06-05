package guillermobeltran.speechrecognition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static guillermobeltran.speechrecognition.R.id.imageButton;
import static guillermobeltran.speechrecognition.R.id.imageButton2;
import static guillermobeltran.speechrecognition.R.id.turnRight;
import static guillermobeltran.speechrecognition.R.id.turnLeft;
import static guillermobeltran.speechrecognition.R.id.okButton;


public class SpeakToMe extends Activity {

    private TextView txtSpeechInput;
    private Bitmap _finalBm;
    private ImageButton _btnSpeak, _btnCam;
    private Button _rButton, _lButton, _okButton;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final String TAG = "SpeakToMe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speak_to_me);
        //Looks for the txtSpeechInput
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        _btnSpeak = (ImageButton) findViewById(imageButton);
        _btnCam = (ImageButton) findViewById(imageButton2);
        _rButton = (Button) findViewById(turnRight);
        _lButton = (Button) findViewById(turnLeft);
        _okButton = (Button) findViewById(okButton);

        // Crashed the app so commented it out
//        getActionBar().hide();
        //when the button gets clicked
        _btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
        _rButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                rotate(1);
            }
        });
        _lButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                rotate(-1);
            }
        });
        _okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                rotate(0);
            }
        });
    }
    public void rotate(int direction){
        Matrix matrix = new Matrix();
        if (direction==1){
            matrix.postRotate(90);
        }else if(direction==-1){
            matrix.preRotate(90);
        }else{
            _rButton.setVisibility(View.GONE);
            _lButton.setVisibility(View.GONE);
            _okButton.setVisibility(View.GONE);
        }
        _finalBm = Bitmap.createBitmap(_finalBm, 0, 0, _finalBm.getWidth(), _finalBm.getHeight(), matrix, true);
        _btnCam.setImageBitmap(_finalBm);
        _btnCam.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }
    public void takePic(View view){
        Intent intent = new Intent(getApplicationContext(), TakePicture.class);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
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
                    txtSpeechInput.setText(result.get(0));//setting the text to what we said
                }
                break;
            }
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    String stringuri = data.getStringExtra("FILE_URI");
                    Uri uri = Uri.parse(stringuri);
                    File file = new File(uri.getPath());
                    ExifInterface rotation = null;
                    try {
                        rotation = new ExifInterface(file.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap scaledbm = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
                    scaledbm = Bitmap.createScaledBitmap(scaledbm, _btnCam.getWidth(), _btnCam.getHeight(), true);
                    _finalBm = Bitmap.createBitmap(scaledbm, 0, 0, scaledbm.getWidth(), scaledbm.getHeight(), null, true);
                    _btnCam.setImageBitmap(_finalBm);
                    _btnCam.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    file.delete();
                    _rButton.setVisibility(View.VISIBLE);
                    _lButton.setVisibility(View.VISIBLE);
                    _okButton.setVisibility(View.VISIBLE);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Operation canceled\n", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =  new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }
    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "SpeechRecog");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("SpeechRecog", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}