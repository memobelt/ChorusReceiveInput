package guillermobeltran.chorusinput;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by sunnysummer5 on 7/28/15.
 */
class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    final int scale;

    public DownloadImageTask(ImageView bmImage, int scale) {
        this.bmImage = bmImage;
        this.scale = scale;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(Bitmap.createScaledBitmap(result, result.getWidth()*scale, result.getHeight()*scale,
                true));
    }
}