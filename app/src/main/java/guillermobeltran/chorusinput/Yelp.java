package guillermobeltran.chorusinput;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Yelp extends Activity {
    Button submit;
    String url;
    EditText city, state, query;
    String text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yelp);

        city = (EditText) findViewById(R.id.city);
        state = (EditText) findViewById(R.id.state);
        query = (EditText) findViewById(R.id.query);

        submit = (Button) findViewById(R.id.submit_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "http://www.yelp.com/search?find_desc="+spaces(query.getText().toString())
                        +"&find_loc="+spaces(city.getText().toString())+"%2C+"+
                        spaces(state.getText().toString())+"&ns=1";

                URL yelp = null;
                BufferedReader in=null;
                try {
                    yelp = new URL(url);
                    in = new BufferedReader(
                            new InputStreamReader(yelp.openStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        text+=inputLine; }
                    in.close();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
                Intent back = new Intent(getApplicationContext(), ChorusChat.class);
                //back.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                back.putExtra("Asking", true);
                back.putExtra("Speech", false);
                back.putExtra("Words", getList(text));
                startActivity(back);
            }
        });
    }

    private String spaces(String s) {
        return s.replace(" ", "+");
    }
    private String getList(String HTML) {
        String temp = "<meta name=\"description\"";
        int start = HTML.indexOf(temp);
        int end = HTML.substring(start).indexOf("/>");
        return HTML.substring(start+temp.length(),end+HTML.substring(0,start).length());
    }
}
