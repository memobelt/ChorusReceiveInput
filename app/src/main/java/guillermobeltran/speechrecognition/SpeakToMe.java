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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static guillermobeltran.speechrecognition.R.id.imageButton;
import static guillermobeltran.speechrecognition.R.id.imageButton2;
import static guillermobeltran.speechrecognition.R.id.imageButton3;


public class SpeakToMe extends Activity {

    private TextView txtSpeechInput;
    private ImageButton _btnSpeak;
    private ImageButton _btnCam;
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
        _btnSpeak = (ImageButton) findViewById(imageButton);
        _btnCam = (ImageButton) findViewById(imageButton2);
        surface = (SurfaceView) findViewById(R.id.surfaceView);
        btnCam2 = (ImageButton) findViewById(imageButton3);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        pictureLayout = (LinearLayout) findViewById(R.id.pictureLayout);
        // Crashed the app so commented it out
//        getActionBar().hide();
        //when the button gets clicked
        _btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
        btnCam2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
//                SetUp(2);
            }
        });
    }
    public void newTake(View view){
//        Intent intent = new Intent(getApplicationContext(), TakePicture.class);
//        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

            // start the image capture Intent
            startActivityForResult(intent, 300);
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
            case 300: {
                if (resultCode == RESULT_OK) {
                    // Video captured and saved to fileUri specified in the Intent
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    _btnCam.setImageBitmap(imageBitmap);
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
            Camera.Parameters param = cam.getParameters();
            param.setFlashMode(param.FLASH_MODE_AUTO);
            param.setFocusMode(param.FOCUS_MODE_CONTINUOUS_PICTURE);
            cam.setParameters(param);
            cam.startPreview();
        }
        return cam;
    }
    public void takePic(final Camera cam){
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
            final Camera.PictureCallback jpeg = new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] bytes, Camera camera) {
                    File file = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(bytes);
                        fos.close();
                        ExifInterface rotation = new ExifInterface(file.getAbsolutePath());
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap scaledbm = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
                        scaledbm = Bitmap.createScaledBitmap(scaledbm, _btnCam.getWidth(), _btnCam.getHeight(), true);
                        Matrix matrix = new Matrix();
                        String a = rotation.getAttribute(rotation.TAG_ORIENTATION);
                        matrix.postRotate(90);
                        Bitmap rotatedbm = Bitmap.createBitmap(scaledbm, 0, 0, scaledbm.getWidth(), scaledbm.getHeight(), matrix, true);
//                    file.delete();
//                    _btnCam.setImageURI(Uri.fromFile(file));
                        _btnCam.setImageBitmap(rotatedbm);
                        _btnCam.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//                        Toast.makeText(getApplicationContext(), "New Image saved:" + file, Toast.LENGTH_LONG).show();
                    } catch (Exception error) {
                        Log.d(TAG, "File" + file
                                + "not saved: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Image could not be saved.", Toast.LENGTH_LONG).show();
                    }
                }
            };
        Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean b, Camera camera) {
                if(b) {
                    cam.takePicture(shutter, null, jpeg);
                }
            }
        };
        cam.autoFocus(autoFocusCallback);
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