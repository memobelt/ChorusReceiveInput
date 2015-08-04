package guillermobeltran.chorusinput.UserManagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import guillermobeltran.chorusinput.MainActivity;
import guillermobeltran.chorusinput.OpenOnPhone;
import guillermobeltran.chorusinput.R;

public class Login extends Activity {

    private TextView mTextView;
    ImageView logo;
    Button login, test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                login(true); //check to see if already logged in on phone

                mTextView = (TextView) stub.findViewById(R.id.text);
                logo = (ImageView) findViewById(R.id.logo);
                login = (Button) findViewById(R.id.btnLogin);
                test = (Button) findViewById(R.id.testLogin);
                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        login(false);
                    }
                });
                test.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        test();
                    }
                });
            }
        });
    }
    private void login(boolean test) {
        Intent intent = new Intent(getApplicationContext(), OpenOnPhone.class);
        if(test) {
            intent.putExtra("caller", "LoginTest");
        }
        else {
            intent.putExtra("caller", "Login");
        }
        startActivity(intent);
    }
    private void test() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
