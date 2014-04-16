package com.example.mapdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;

public class DispatchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ParseUser.getCurrentUser() != null) {
            startActivity(new Intent(this, MapDemoActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
