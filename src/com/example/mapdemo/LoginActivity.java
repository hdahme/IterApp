package com.example.mapdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends Activity {
    private EditText etUsername;
    private EditText etPassword;
    private EditText etEmail;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		this.etUsername = (EditText) findViewById(R.id.etUsername);
		this.etPassword = (EditText) findViewById(R.id.etPassword);
		this.etEmail = (EditText) findViewById(R.id.etEmail);

		ParseAnalytics.trackAppOpened(getIntent());
	}

	public void onSignUp(View v) {
	    String username = this.etUsername.getText().toString();
	    String password = this.etPassword.getText().toString();
	    String email = this.etEmail.getText().toString();

	    if (username == null || username.length() == 0 ||
	        password == null || password.length() == 0 ||
	        email == null || email.length() == 0) {
	        Log.d("DEBUG", "Bad login credentials");
	        return;
	    }

	    ParseUser user = new ParseUser();
	    user.setUsername(username);
	    user.setPassword(password);
	    user.setEmail(email);
	    user.signUpInBackground(new SignUpCallback() {
	        public void done(ParseException e) {
	            if (e == null) {
	                // Hooray! Let them use the app now.
	                Log.d("DEBUG", "Signed up! Starting new activity");
	                Intent i = new Intent(LoginActivity.this, MapDemoActivity.class);
	                startActivity(i);
	            } else {
	                // Sign up didn't succeed. Look at the ParseException
	                // to figure out what went wrong
	                Log.d("DEBUG", "Error signing up: " + e.toString());
	            }
	        }
	    });
	}

	public void onLogin(View v) {
        String username = this.etUsername.getText().toString();
        String password = this.etPassword.getText().toString();
        String email = this.etEmail.getText().toString();

        if (username == null || username.length() == 0 ||
            password == null || password.length() == 0 ||
            email == null || email.length() == 0) {
            Log.d("DEBUG", "Bad login credentials");
            return;
        }

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    Log.d("DEBUG", "Logged in! Starting new activity");
                    Intent i = new Intent(LoginActivity.this, MapDemoActivity.class);
                    startActivity(i);
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Log.d("DEBUG", "Error logging in: " + e.toString());
                }
            }
        });
    }
}
