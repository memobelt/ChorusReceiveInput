package guillermobeltran.speechrecognition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static guillermobeltran.speechrecognition.R.id.surfaceView;
import static guillermobeltran.speechrecognition.R.id.takePicture;
import static guillermobeltran.speechrecognition.R.id.picturePreview;

/*
TODO: Fix the landscape error. Figure out whether the picture is taken in landscape or not and send that in intent? Then intent takes care of it?
A bit hacky but bette than having landscaped photo come out as portrait.
 */
public class TakePicture extends Activity {

    private ImageButton _btnCam;
    private SurfaceView _surface;
    private Camera _camera;
    private Uri _uri;
    private static final String TAG = "TakePicture";
    public static final int MEDIA_TYPE_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);
        _btnCam = (ImageButton) findViewById(takePicture);
        _surface = (SurfaceView) findViewById(picturePreview);
        _btnCam.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                takePic();
            }
        });
        int numCams = Camera.getNumberOfCameras();
        _camera = null;
        Boolean isBack = false;
        if (numCams > 0) {
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
                            Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                        }
                    }
                }
            }
            SurfaceHolder surfaceHolder = _surface.getHolder();
            SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    try {
                        _camera.setPreviewDisplay(surfaceHolder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                    Log.d(TAG,"Surface Changed");
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    Log.d(TAG,"Surface destroyed");
                    _camera.release();
                }
            };
            surfaceHolder.addCallback(callback);

            Camera.Parameters param = _camera.getParameters();
            param.setFlashMode(param.FLASH_MODE_AUTO);
            param.setFocusMode(param.FOCUS_MODE_CONTINUOUS_PICTURE);
            _camera.setParameters(param);
            _camera.startPreview();
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
    public void takePic(){
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
                _uri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                File file = new File(_uri.getPath());
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bytes);
                    fos.close();
//                    ExifInterface rotation = new ExifInterface(file.getAbsolutePath());
//                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//                    Bitmap scaledbm = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
//                    scaledbm = Bitmap.createScaledBitmap(scaledbm, _btnCam.getWidth(), _btnCam.getHeight(), true);
//                    Matrix matrix = new Matrix();
//                    String a = rotation.getAttribute(rotation.TAG_ORIENTATION);
//                    matrix.postRotate(90);
//                    Bitmap rotatedbm = Bitmap.createBitmap(scaledbm, 0, 0, scaledbm.getWidth(), scaledbm.getHeight(), matrix, true);
//        //                    file.delete();
//                    _camera.release();
//        //                    btnCam.setImageURI(Uri.fromFile(file));
//                    _btnCam.setImageBitmap(rotatedbm);
//                    btnCam.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//                    Toast.makeText(getApplicationContext(), "New Image saved:" + file, Toast.LENGTH_LONG).show();
                } catch (Exception error) {
                    Log.d(TAG, "File" + file
                            + "not saved: " + error.getMessage());
//                    Toast.makeText(getApplicationContext(), "Image could not be saved.", Toast.LENGTH_LONG).show();
                }
                Intent intent = new Intent();
                if(_uri != null){
                    setResult(RESULT_OK, intent);
                    intent.putExtra("FILE_URI",_uri.toString());
                }else{
                    setResult(0,intent);
                }
                finish();
            }
        };
        Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean b, Camera camera) {
                if(b) {
                    _camera.takePicture(shutter, null, jpeg);
                }
            }
        };
        _camera.autoFocus(autoFocusCallback);
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
        } else {
            return null;
        }

        return mediaFile;
    }
}
