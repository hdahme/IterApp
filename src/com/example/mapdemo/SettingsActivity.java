package com.example.mapdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.parse.ParseUser;

public class SettingsActivity extends Activity {
    private TextView tvUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        this.tvUsername = (TextView) findViewById(R.id.tvUsername);
        
        ParseUser currUser = ParseUser.getCurrentUser();
        this.tvUsername.setText(currUser.getString("name"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    public void onLogout(View v) {
        ParseUser.logOut();
        Intent i = new Intent(this, DispatchActivity.class);
        startActivity(i);
    }
}
