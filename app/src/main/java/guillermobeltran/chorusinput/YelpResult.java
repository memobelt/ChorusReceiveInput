package guillermobeltran.chorusinput;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
/*
Created by Summer
 */
public class YelpResult extends ActionBarActivity {
    ListView listView;
    Button send;
    ImageView image, rating;
    TextView reviews;
    String message="";
    private ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yelp_result);
        listView = (ListView) findViewById(R.id.listView);
        send = (Button) findViewById(R.id.send_button);
        image = (ImageView) findViewById(R.id.image);
        rating = (ImageView) findViewById(R.id.rating);
        reviews = (TextView) findViewById(R.id.reviews);
        reviews.setText(getIntent().getStringExtra("reviews"));
        String[] values = new String[] {getIntent().getStringExtra("name"), getIntent().getStringExtra("snippet"),
                getIntent().getStringExtra("url"), getIntent().getStringExtra("location"),
                getIntent().getStringExtra("phone"), getIntent().getStringExtra("deals")};
        mAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,
                android.R.id.text1, values){
            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View view =super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);

                            /*YOUR CHOICE OF COLOR*/
                //unselected
                textView.setTextColor(Color.BLACK);
                textView.setBackgroundColor(Color.WHITE);

                return view;
            }
        };
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                if(textView.getCurrentTextColor()==Color.BLACK) {
                    //unselected -> selected
                    textView.setTextColor(Color.WHITE);
                    textView.setBackgroundColor(Color.BLACK);
                    //add selected information to message string
                    message = message + parent.getItemAtPosition(position)+ " ";
                }
                else {
                    //selected -> unselected
                    textView.setTextColor(Color.BLACK);
                    textView.setBackgroundColor(Color.WHITE);
                    //remove the unselected information from message string
                    message = message.replace((CharSequence) parent.getItemAtPosition(position), "");
                }
            }
        });
        //Linkify.addLinks(text, Linkify.ALL);
        new DownloadImageTask(image, 4).execute(getIntent().getStringExtra("image"));
        new DownloadImageTask(rating, 2).execute(getIntent().getStringExtra("rating"));
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(message.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Select to send", Toast.LENGTH_SHORT).show();
                }
                else {
                    sendButton(message);
                }
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
        intent.putExtra("Asking", false);
        intent.putExtra("Speech", false);
        intent.putExtra("Yelp", true);
        intent.putExtra("ChatNum", getIntent().getStringExtra("ChatNum"));
        intent.putExtra("Role", "crowd");
        startActivity(intent);
    }
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
}
