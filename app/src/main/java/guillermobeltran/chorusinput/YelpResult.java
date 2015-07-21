package guillermobeltran.chorusinput;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.io.InputStream;
import java.util.HashMap;

import guillermobeltran.chorusinput.PushService.ParseUtils;

public class YelpResult extends ActionBarActivity {
    TextView text;
    Button send;
    ImageView image, rating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yelp_result);

        text = (TextView) findViewById(R.id.text);
        send = (Button) findViewById(R.id.send_button);
        image = (ImageView) findViewById(R.id.image);
        rating = (ImageView) findViewById(R.id.rating);

        text.setText(getIntent().getStringExtra("name")+"\n"+
                getIntent().getStringExtra("url")+"\n"+
                getIntent().getStringExtra("location") +"\n"+
                getIntent().getStringExtra("phone"));
        new DownloadImageTask(image).execute(getIntent().getStringExtra("image"));
        new DownloadImageTask(rating).execute(getIntent().getStringExtra("rating"));
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendButton(text.getText().toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_yelp_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendButton(String words) {
        Intent intent = new Intent(getApplicationContext(), ChorusChat.class);
        intent.putExtra("Words", words);
        intent.putExtra("Asking", true);
        intent.putExtra("Speech", false);
        intent.putExtra("ChatNum", "6");
        intent.putExtra("Role", "crowd");

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("email", ParseUtils.customIdBuilder("6"));
        params.put("role", "crowd");
        params.put("task", "6");
        params.put("message", words);
        ParseCloud.callFunctionInBackground("sendPushToUser", params, new FunctionCallback<String>() {
            public void done(String success, ParseException e) {
                if (e == null) {
                    Log.e("ChorusChat", "Push sent successfully.");
                }
            }
        });
        startActivity(intent);
    }
    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
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
            bmImage.setImageBitmap(result);
        }
    }
}
