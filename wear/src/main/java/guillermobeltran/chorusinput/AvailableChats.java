package guillermobeltran.chorusinput;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/*
Created by Summer Kitahara.
These are the available chats that the crowd can enter and answer questions.
This is where the Chat numbers are populated. Right now it's just 1-20.
 */
public class AvailableChats extends Activity {

    private TextView mTextView;
    private ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_chats2);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mListView = (ListView) findViewById(R.id.listView);

                // This is the adapter that display the content.
                ListAdapter mAdapter = new ArrayAdapter<guillermobeltran.chorusinput.Chats.AvailableChats.ChatNumber>
                        (getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1,
                        guillermobeltran.chorusinput.Chats.AvailableChats.ITEMS){
                    @Override
                    public View getView(int position, View convertView,
                                        ViewGroup parent) {
                        View view =super.getView(position, convertView, parent);

                        TextView textView=(TextView) view.findViewById(android.R.id.text1);

                            /*YOUR CHOICE OF COLOR*/
                        textView.setTextColor(Color.BLACK);
                        textView.setGravity(Gravity.CENTER);

                        return view;
                    }
                };

                // Set the adapter
                mListView.setAdapter(mAdapter);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getApplicationContext(), ChorusChat.class);
                        intent.putExtra("ChatNum", Integer.toString(position + 1));
                        intent.putExtra("caller", "MainActivity");
                        startActivity(intent);
                    }
                });
            }
        });
    }
}
