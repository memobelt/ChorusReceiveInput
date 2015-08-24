package guillermobeltran.chorusinput;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/*
Created by Summer Kitahara
In this activity, the Yelp API is queried with the search term and location from SearchBarActivity
 */
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
                //fills listview with businesses
                getListView().setAdapter(new ArrayAdapter<Business>(getApplicationContext(),
                        android.R.layout.simple_list_item_1, businesses) {
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
                });
            }
        }.execute();
    }

    //send info about business to Yelp search result page
    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        Business biz = (Business)listView.getItemAtPosition(position);
        Intent intent = new Intent(getApplicationContext(), YelpResult.class);
        intent.putExtra("name", biz.name);
        intent.putExtra("url", biz.url);
        intent.putExtra("location", biz.location);
        intent.putExtra("phone", biz.phone);
        intent.putExtra("image", biz.image);
        intent.putExtra("rating", biz.rating);
        intent.putExtra("deals", biz.deals);
        intent.putExtra("snippet", biz.snippet);
        intent.putExtra("reviews", Integer.toString(biz.reviews) + " reviews");
        intent.putExtra("ChatNum", getIntent().getStringExtra("ChatNum"));
        startActivity(intent);
    };

    //parse to extract information by keys
    List<Business> processJson(String jsonStuff) throws JSONException {
        JSONObject json = new JSONObject(jsonStuff);
        JSONArray businesses = json.getJSONArray("businesses");
        ArrayList<Business> businessObjs = new ArrayList<Business>(businesses.length());
        for (int i = 0; i < businesses.length(); i++) {
            JSONObject business = businesses.getJSONObject(i);
            JSONArray address = business.getJSONObject("location").getJSONArray("display_address");
            JSONArray deals = null;
            try {
                deals = business.getJSONArray("deals");
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.e("error", e.toString());
            }
            String address_string = "";
            for (int j = 0; j < address.length(); j++) {
                address_string = address_string + address.getString(j) + " ";
            }
            String deals_string = "";
            if(deals == null){
                deals_string = "No promotions";
            }
            else {
                for (int j = 0; i < deals.length(); j++) {
                    deals_string = deals_string + deals.getString(j) + ", ";
                }
            }
            businessObjs.add(new Business(business.optString("name"), business.optString("mobile_url"),
                    business.optString("display_phone"), address_string, business.optString("image_url"),
                    business.optString("rating_img_url_large"), deals_string, business.optString("snippet_text"),
                    business.optInt("review_count")));
        }
        return businessObjs;
    }

    class Business {
        final String name;
        final String url;
        final String phone;
        String location;
        final String image;
        final String rating;
        final String deals;
        final String snippet;
        final int reviews;

        public Business(String name, String url, String phone, String location,
                        String image, String rating, String deals, String snippet, int reviews) {
            this.name = name;
            this.url = url;
            this.phone = phone;
            this.location = location;
            this.image = image;
            this.rating = rating;
            this.deals = deals;
            this.snippet = snippet;
            this.reviews = reviews;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}