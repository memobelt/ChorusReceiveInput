package guillermobeltran.chorusinput;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private TextView mTextView;
    float x1,x2,y1,y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mTextView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch(event.getAction()) {
                            case MotionEvent.ACTION_DOWN: {
                                x1 = event.getX();
                                y1 = event.getY();
                                break;
                            }
                            case MotionEvent.ACTION_UP: {
                                x2 = event.getX();
                                y2 = event.getY();
                                //left to right sweep
                                /*if (x1 < x2 && Math.abs(x2-x1) > Math.abs(y2-y1)) {
                                    Toast.makeText(getApplicationContext(), "left to right swipe", Toast.LENGTH_SHORT).show();
                                    break;
                                }*/
                                //right to left sweep. Review: only shows most recent post to ChorusChat
                                if (x1 > x2 && Math.abs(x2-x1) > Math.abs(y2-y1)) {
                                    //Toast.makeText(getApplicationContext(), "right to left swipe", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this, ChorusChat.class);
                                    intent.putExtra("caller", "MainActivity");
                                    startActivity(intent);
                                    break;
                                }
                                //up to down sweep. Ask
                                if (y1 < y2) {
                                    //Toast.makeText(getBaseContext(), "up to down swipe", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this, Microphone.class);
                                    startActivity(intent);
                                    break;
                                }
                                //down to up sweep. Answer: open on phone
                                if (y2 < y1) {
                                    //Toast.makeText(getBaseContext(), "down to up swipe", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this, OpenOnPhone.class);
                                    intent.putExtra("caller", "MainActivity");
                                    startActivity(intent);
                                    break;
                                }
                                break;
                            }
                            case MotionEvent.ACTION_CANCEL: {
                                //Toast.makeText(getBaseContext(), "Action was cancel", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case MotionEvent.ACTION_OUTSIDE: {
                                Toast.makeText(getBaseContext(), "Movement occured outside current screen element",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                        return true;
                    }
                });
                TextView tv = (TextView) stub.findViewById(R.id.review);
                tv.setRotation(90);
            }
        });
    }
}