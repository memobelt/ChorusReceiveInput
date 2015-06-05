package guillermobeltran.speechrecognition;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static guillermobeltran.speechrecognition.R.id.imageButton;
import static guillermobeltran.speechrecognition.R.id.imageButton2;


public class SpeakToMe extends Activity {

    private TextView txtSpeechInput;
    private ImageButton _btnSpeak;
    private ImageButton _btnCam;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 200;
    private final int CAPTURE_DEVICE_IMAGE = 300;
    private static final String TAG = "SpeakToMe";
    private String _mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speak_to_me);
        //Looks for the txtSpeechInput
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        _btnSpeak = (ImageButton) findViewById(imageButton);
        _btnCam = (ImageButton) findViewById(imageButton2);
        _btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
    }
    public void newTake(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = getOutputMediaFile();
        if (photoFile != null){
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photoFile));
            startActivityForResult(intent, CAPTURE_DEVICE_IMAGE);
        }
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
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    Bitmap rotatedbm = Bitmap.createBitmap(scaledbm, 0, 0, scaledbm.getWidth(), scaledbm.getHeight(), matrix, true);
                    _btnCam.setImageBitmap(rotatedbm);
                    _btnCam.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    file.delete();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Operation failed\n", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Operation failed\n", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case CAPTURE_DEVICE_IMAGE: {
                if (resultCode == RESULT_OK) {
                    // Video captured and saved to fileUri specified in the Intent
//                    Bundle extras = data.getExtras();
//                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    File f = new File(_mCurrentPhotoPath);
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap scaledbm = BitmapFactory.decodeFile(f.getAbsolutePath(), bmOptions);
                    scaledbm = Bitmap.createScaledBitmap(scaledbm, _btnCam.getWidth(), _btnCam.getHeight(), true);
                    _btnCam.setImageBitmap(scaledbm);
                    _btnCam.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//
                } else {
                    Toast.makeText(this, "Operation failed\n", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }


    private  Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile());
    }
    /** Create a File for saving an image or video */
    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), ".SpeechRecog");
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
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    mediaStorageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Save a file: path for use with ACTION_VIEW intents
        _mCurrentPhotoPath =  image.getAbsolutePath();
        return image;
    }
}