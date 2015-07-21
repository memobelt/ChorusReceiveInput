package guillermobeltran.chorusinput;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Yelp extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setTitle("Searching...");

        Intent intent = getIntent();
        final String searchTerm = intent.getData().getQueryParameter("term");
        final String searchLocation = intent.getData().getQueryParameter("location");

        setProgressBarIndeterminateVisibility(true);
        new AsyncTask<Void, Void, List<Business>>() {
            @Override
            protected List<Business> doInBackground(Void... params) {
                String businesses = YelpAPI.getYelp(Yelp.this).searchForBusinessesByLocation(searchTerm, searchLocation);
                try {
                    return processJson(businesses);
                } catch (JSONException e) {
                    return Collections.<Business>emptyList();
                }
            }

            @Override
            protected void onPostExecute(List<Business> businesses) {
                setTitle(searchTerm+" in "+searchLocation);
                setProgressBarIndeterminateVisibility(false);
                getListView().setAdapter(new ArrayAdapter<Business>(getApplicationContext(),
                        android.R.layout.simple_list_item_1, businesses));
            }
        }.execute();
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        Business biz = (Business)listView.getItemAtPosition(position);
        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(biz.url)));
        Intent intent = new Intent(getApplicationContext(), YelpResult.class);
        intent.putExtra("name", biz.name);
        //intent.putExtra("url", Uri.parse(biz.url));
        intent.putExtra("url", biz.url);
        intent.putExtra("rating", biz.rating);
        //intent.putExtra("location", biz.location);
        intent.putExtra("phone", biz.phone);
        intent.putExtra("image", biz.image);
        startActivity(intent);

    };

    List<Business> processJson(String jsonStuff) throws JSONException {
        JSONObject json = new JSONObject(jsonStuff);
        JSONArray businesses = json.getJSONArray("businesses");
        ArrayList<Business> businessObjs = new ArrayList<Business>(businesses.length());
        for (int i = 0; i < businesses.length(); i++) {
            JSONObject business = businesses.getJSONObject(i);
            businessObjs.add(new Business(business.optString("name"), business.optString("mobile_url"),
                    business.optInt("rating"), business.optString("display_phone"),
                    business.optString("image_url")));
        }
        return businessObjs;
    }

    class Business {
        final String name;
        final String url;
        final int rating;
        final String phone;
        final String image;

        public Business(String name, String url, int rating,String phone,
                        String image) {
            this.name = name;
            this.url = url;
            this.rating = rating;
            this.phone = phone;
            this.image = image;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}