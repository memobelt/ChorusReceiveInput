package guillermobeltran.chorusinput.UserManagement;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import guillermobeltran.chorusinput.AfterLogin;
import guillermobeltran.chorusinput.OpenOnWatch;
import guillermobeltran.chorusinput.R;

/**
 * Created by jason on 2015/7/29.
 */
public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private ImageView logo;
//    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        logo = (ImageView) findViewById(R.id.logo);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());


        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    AfterLogin.class);
            startActivity(intent);
            loginWatch();
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString();
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();

                if (!name.isEmpty() && !password.isEmpty()) {
                    pDialog.setMessage("Registering ...");
                    showDialog();
                    ParseUser user = new ParseUser();
                    user.setUsername(name);
                    user.setPassword(password);
                    if (!email.equals("")) {
                        user.setEmail(email);
                    }
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            hideDialog();
                            if (e == null) {
                                Log.d(TAG, "Sign up successfully!");
                                Intent intent = new Intent(
                                        RegisterActivity.this,
                                        LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
//                                Toast.makeText(getApplicationContext(),
//                                        "Username already used!", Toast.LENGTH_LONG).show();
                                Toast.makeText(getApplicationContext(),
                                        e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    private void loginWatch() {
        Intent intent = new Intent(getApplicationContext(), OpenOnWatch.class);
        intent.putExtra("Text", false);
        intent.putExtra("Login", true);
        startActivity(intent);
    }
}
