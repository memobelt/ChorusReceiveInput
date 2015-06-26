package guillermobeltran.chorusinput;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static guillermobeltran.chorusinput.R.id.frame;
import static guillermobeltran.chorusinput.R.id.pictureLayout;
import static guillermobeltran.chorusinput.R.id.setPicture;


public class OrientPicture extends Activity {
    private ImageButton _picture;
    private FrameLayout _frameLayout;
    private LinearLayout _pictureLayout;
    private Bitmap _finalBm;
    private Uri _uri;
    private File _file;
    private static final String TAG = "OrientPicture";

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orient_picture);
        String stringuri = getIntent().getStringExtra("FILE_URI");
        _uri = Uri.parse(stringuri);
        _file = new File(_uri.getPath());
        _pictureLayout = (LinearLayout) findViewById(pictureLayout);
        _frameLayout = (FrameLayout) findViewById(frame);
        _picture = (ImageButton) findViewById(setPicture);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap scaledbm = BitmapFactory.decodeFile(_file.getAbsolutePath(), bmOptions);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        scaledbm = Bitmap.createScaledBitmap(scaledbm, width, height, true);
        _finalBm = Bitmap.createBitmap(scaledbm, 0, 0, scaledbm.getWidth(), scaledbm.getHeight(), null, true);
        //set camera button to that of bitmap and deletes file
        _picture.setImageBitmap(_finalBm);
        _picture.setScaleType(ImageView.ScaleType.FIT_XY);
    }


    public void turnRight(View view){
        rotate("RIGHT");
    }
    public void turnLeft(View view){
        rotate("LEFT");
    }
    public void ok (View view){
        rotate("OK");
    }
    public void cancel(View view){
        rotate("CANCEL");
    }

    public void rotate(String direction){
        Matrix matrix = new Matrix();
        if (direction == "OK"){
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            intent.putExtra("FILE_URI",_uri.toString());
            try {
                FileOutputStream fos = new FileOutputStream(new File(_uri.getPath()));
                _finalBm.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
            finish();
        }
        else if (direction == "LEFT"){
            matrix.postRotate(-90);
            setBitMap(matrix);
        }
        else if(direction == "RIGHT"){
            matrix.postRotate(90);
            setBitMap(matrix);
        }
        else if(direction == "CANCEL") {
            //Removes the options from view
            _file.delete();
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }
    public void setBitMap(Matrix matrix){
        _finalBm = Bitmap.createBitmap(_finalBm, 0, 0, _finalBm.getWidth(), _finalBm.getHeight(), matrix, true);
        _picture.setImageBitmap(_finalBm);
        _picture.setScaleType(ImageView.ScaleType.FIT_XY);
    }
}
