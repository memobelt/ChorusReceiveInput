package guillermobeltran.chorusinput;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SearchBarActivity extends Activity {

    private EditText mSearchTerm;
    private EditText mSearchLocation;
    Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bar);
        setTitle("Search Yelp");
        mSearchTerm = (EditText)findViewById(R.id.searchTerm);
        mSearchLocation = (EditText)findViewById(R.id.searchLocation);
        search = (Button) findViewById(R.id.button1);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
    }

    public void search() {
        String term = mSearchTerm.getText().toString();
        String location = mSearchLocation.getText().toString();
        Intent intent = new Intent(this, Yelp.class);
        intent.setData(new Uri.Builder().appendQueryParameter("term", term).appendQueryParameter("location", location).build());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Bundle extras = getIntent().getExtras();
        String id = null;
        if (extras != null) {
            id = extras.getString("taskId");
        }
        Intent intent = new Intent(this, ChorusChat.class);
        intent.putExtra("ChatNum",id);
        intent.putExtra("Asking",false);
        intent.putExtra("Role","crowd");
        startActivity(intent);
    }

}
