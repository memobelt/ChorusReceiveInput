package guillermobeltran.chorusinput;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static guillermobeltran.chorusinput.R.id.picturePreview;
import static guillermobeltran.chorusinput.R.id.takePicture;
/*
Unused. To take picture. Not needed because server can't handle pictures. Not the best thing.
Pictures come out wrong and user needs to fix it. Pretty bad. Good luck.
 */

public class TakePicture extends Activity {

    private ImageButton _btnCam;
    private SurfaceView _surface;
    private ImageView _picture;
    private FrameLayout _frameLayout;
    private LinearLayout _pictureLayout;
    private Bitmap _finalBm;
    private Camera _camera;
    private Uri _uri;
    private Boolean _calledHome, _pictureTaken;
    private static final String TAG = "TakePicture";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);
        //initialize relevant materials
        _btnCam = (ImageButton) findViewById(takePicture);
        _surface = (SurfaceView) findViewById(picturePreview);
        _calledHome = false;//this is to keep track of whether the user went back home
        _btnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePic();
            }
        });
        //get the back facing camera and if there is none get the front facing camera
        if (_pictureTaken == null || _pictureTaken == false) {
            if (Camera.getNumberOfCameras() > 0) {
                _camera = null;
                initializeCamera();
            } else {//if there are no cameras
                Intent intent = new Intent();
                setResult(RESULT_FIRST_USER, intent);
                finish();
            }
        }
    }

    public void initializeCamera(){
        int numCams = Camera.getNumberOfCameras();
        Boolean isBack = false;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int camID = 0; camID < numCams; camID++) {
            Camera.getCameraInfo(camID, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                isBack = true;
                try {
                    _camera = Camera.open(camID);
                    setCameraDisplayOrientation(this, camID, _camera);
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
                        _camera = Camera.open(camID);
                        setCameraDisplayOrientation(this, camID, _camera);
                    } catch (RuntimeException e) {
                        Log.e(TAG, "Front Camera failed to open: " + e.getLocalizedMessage());
                    }
                }
            }
        }
        SurfaceHolder surfaceHolder = _surface.getHolder();
        //callback ensures the preview display is ready before it is set
        SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (_calledHome){//if the user pressed home we will go back to SpeakToMe
                    Intent intent = new Intent();
                    setResult(RESULT_FIRST_USER,intent);
                    finish();
                }
                else {
                    try {//setDisplay
                        _camera.setPreviewDisplay(surfaceHolder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                Log.d(TAG,"Surface Changed");
            }

            //when surface is destroyed camera must be released
            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                Log.d(TAG, "Surface destroyed");
                _camera.release();
                _calledHome = true;
            }
        };
        surfaceHolder.addCallback(callback);
        //set parameters
        Camera.Parameters param = _camera.getParameters();
        param.setFlashMode(param.FLASH_MODE_AUTO);
        param.setFocusMode(param.FOCUS_MODE_CONTINUOUS_PICTURE);
        _camera.setParameters(param);
        _camera.startPreview();
    }
    /*
    Set's the display orientation of the camera to match that of the phone. Found online.
     */
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
    /*
    Take the picture and saves it to a newly created file
     */
    public void takePic(){
        //It's supposed to play a shutter sound when camera is clicked. Don't think it works
        final Camera.ShutterCallback shutter = new Camera.ShutterCallback() {
            @Override
            public void onShutter() {
                AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
            }
        };
        //What to do when the picture is taken. It saves the picture to a file places the URI in an intent
        final Camera.PictureCallback jpeg = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                _uri = getOutputMediaFileUri();
                File file = new File(_uri.getPath());
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bytes);
                    fos.close();
                } catch (Exception error) {
                    Log.d(TAG, "File" + file
                            + "not saved: " + error.getMessage());
                }
                _camera.release();
                Intent OrientIntent = new Intent(getApplicationContext(), OrientPicture.class);
                OrientIntent.putExtra("FILE_URI", _uri.toString());
                startActivityForResult(OrientIntent,0);
            }
        };
        //Waits for the camera to focus before taking a picture
        Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean b, Camera camera) {
                if(b) {
                    _camera.takePicture(shutter, null, jpeg);
                }
            }
        };
        //Starts the picture taking
        _camera.autoFocus(autoFocusCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            Intent intent = data;
            setResult(RESULT_OK,intent);
            finish();
        }
        else{
            Intent intent = data;
            setResult(100,intent);
            finish();
        }
    }

    /*
        Get uri from created file
         */
    private static Uri getOutputMediaFileUri(){
        return Uri.fromFile(getOutputMediaFile());
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(){
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
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");

        return mediaFile;
    }
}
