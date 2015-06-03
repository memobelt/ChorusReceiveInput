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
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static guillermobeltran.speechrecognition.R.id.imageButton;
import static guillermobeltran.speechrecognition.R.id.imageButton2;
import static guillermobeltran.speechrecognition.R.id.imageButton3;
import static guillermobeltran.speechrecognition.R.id.linearLayout;
import static guillermobeltran.speechrecognition.R.id.pictureLayout;
import static guillermobeltran.speechrecognition.R.id.surfaceView;


public class SpeakToMe extends Activity {

    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private ImageButton btnCam;
    private ImageButton btnCam2;
    private SurfaceView surface;
    private LinearLayout linearLayout;
    private LinearLayout pictureLayout;
    private Camera _camera;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 200;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 300;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final String TAG = "SpeakToMe";
    private Uri fileUri;
    private Uri oldfileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speak_to_me);
        //Looks for the txtSpeechInput
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(imageButton);
        btnCam = (ImageButton) findViewById(imageButton2);
        surface = (SurfaceView) findViewById(R.id.surfaceView);
        btnCam2 = (ImageButton) findViewById(imageButton3);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        pictureLayout = (LinearLayout) findViewById(R.id.pictureLayout);

        // Crashed the app so commented it out
//        getActionBar().hide();
        //when the button gets clicked
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
        btnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Camera.getNumberOfCameras()>0) {
                    _camera = setPrev();
                    txtSpeechInput.setVisibility(view.GONE);
//                int vis = txtSpeechInput.getVisibility();
                    linearLayout.setVisibility(view.GONE);
                    pictureLayout.setVisibility(view.VISIBLE);
                }
            }
        });
        btnCam2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                takePic(_camera);
                pictureLayout.setVisibility(v.GONE);
                txtSpeechInput.setVisibility(v.VISIBLE);
                linearLayout.setVisibility(v.VISIBLE);
            }
        });
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
                    // Image captured and saved to fileUri specified in the Intent

                    btnCam.setImageURI(fileUri);
                    btnCam.setScaleType(ImageView.ScaleType.CENTER);

//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inJustDecodeBounds = true;
//                    BitmapFactory.decodeFile(fileUri.getPath(), options);
//                    int width = options.outWidth;
//                    int height = options.outHeight;
//
//                    android.view.ViewGroup.LayoutParams layoutParams = btnCam.getLayoutParams();
//                    if(layoutParams.height > layoutParams.width) {
//                        int tempWidth = layoutParams.width;
//                        layoutParams.width = layoutParams.height;
//                        layoutParams.height = tempWidth;
//                        btnSpeak.setImageURI(fileUri);
//                    }
//                    else{
//                        int tempWidth = layoutParams.width;
//                        layoutParams.width = layoutParams.height;
//                        layoutParams.height = tempWidth;
//                    }
//                    btnCam.setLayoutParams(layoutParams);
                    if (oldfileUri != null){
                        File file = new File(oldfileUri.getPath());
                        if (file.exists()){
                            file.delete();
                        }
//                        getApplicationContext().getContentResolver().delete(oldfileUri,null,null);
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Operation failed\n", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Operation failed\n", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    // Video captured and saved to fileUri specified in the Intent
                    Toast.makeText(this, "Video saved to:\n" +
                            data.getData(), Toast.LENGTH_LONG).show();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Operation failed\n", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Operation failed\n", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    public Camera setPrev(){
        int numCams = Camera.getNumberOfCameras();
        Camera cam = null;
        Boolean isBack = false;
        if (numCams > 0) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int camID = 0; camID < numCams; camID++) {
                Camera.getCameraInfo(camID, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    isBack = true;
                    try {
                        cam = Camera.open(camID);
                        setCameraDisplayOrientation(this, camID, cam);
                        break;
                    } catch (RuntimeException e) {
                        Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                    }
                }
            }
            if (!isBack) {
                for (int camID = 0; camID < numCams; camID++) {
                    Camera.getCameraInfo(camID, cameraInfo);
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        try {
                            cam = Camera.open(camID);
                            setCameraDisplayOrientation(this, camID, cam);
                        } catch (RuntimeException e) {
                            Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                        }
                    }
                }
            }
//            final LinearLayout layOut = (LinearLayout) findViewById(linearLayout);
//            layOut.setVisibility(View.INVISIBLE);
//            surfaceView.setVisibility(View.VISIBLE);
//            txtSpeechInput.setVisibility(View.INVISIBLE);
            SurfaceHolder surfaceHolder = surface.getHolder();
            //        surfaceHolder.addCallback(this);
            try {
                cam.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            cam.startPreview();
        }
            return cam;
    }
    /*
    TODO: Fix the orientation problem. Delete previous photo if new one is selected. Figure out why it doesn't work on the first iteration. Scaling needs help.
     */
    public void takePic(Camera cam){
//        Context context = this;
//        PackageManager packageManager = context.getPackageManager();
//        if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            if (fileUri != null){
//                oldfileUri = fileUri;
//            }
//            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
//
//            // start the image capture Intent
//            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
//        }
//        else {
//            Toast.makeText(this, "This device does not have a camera.", Toast.LENGTH_SHORT).show();
//        }
//        int numCams = Camera.getNumberOfCameras();
//        Camera cam = null;
//        Boolean isBack = false;
//        if (numCams > 0){
//            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
//            for(int camID = 0; camID < numCams; camID++) {
//                Camera.getCameraInfo(camID, cameraInfo);
//                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                    isBack = true;
//                    try {
//                        cam = Camera.open(camID);
//                        setCameraDisplayOrientation(this, camID, cam);
//                        break;
//                    } catch (RuntimeException e) {
//                        Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
//                    }
//                }
//            }
//            if (!isBack){
//                for(int camID = 0; camID < numCams; camID++) {
//                    Camera.getCameraInfo(camID, cameraInfo);
//                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//                        try {
//                            cam = Camera.open(camID);
//                            setCameraDisplayOrientation(this, camID, cam);
//                        } catch (RuntimeException e) {
//                            Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
//                        }
//                    }
//                }
//            }
////            final LinearLayout layOut = (LinearLayout) findViewById(linearLayout);
////            layOut.setVisibility(View.INVISIBLE);
////            surfaceView.setVisibility(View.VISIBLE);
////            txtSpeechInput.setVisibility(View.INVISIBLE);
//            SurfaceHolder surfaceHolder = surface.getHolder();
//    //        surfaceHolder.addCallback(this);
//            try {
//                cam.setPreviewDisplay(surfaceHolder);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            cam.startPreview();
            final Camera.ShutterCallback shutter = new Camera.ShutterCallback() {
                @Override
                public void onShutter() {
                    AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
                }
            };
            final Camera finalCam = cam;
            final Camera.PictureCallback jpeg = new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] bytes, Camera camera) {
                    File file = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(bytes);
                        fos.close();
                        Toast.makeText(getApplicationContext(), "New Image saved:" + file, Toast.LENGTH_LONG).show();
                    } catch (Exception error) {
                        Log.d(TAG, "File" + file
                                + "not saved: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Image could not be saved.", Toast.LENGTH_LONG).show();
                    }
//                    layOut.setVisibility(View.VISIBLE);
//                    surfaceView.setVisibility(View.INVISIBLE);
//                    txtSpeechInput.setVisibility(View.VISIBLE);
                    finalCam.release();
                    btnCam.setImageURI(Uri.fromFile(file));
                    btnCam.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }
            };
        cam.takePicture(shutter,null,jpeg);
    }
//        else{
//            Toast.makeText(this, "This device does not have a camera.", Toast.LENGTH_SHORT).show();
//        }

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