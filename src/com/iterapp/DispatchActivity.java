package com.iterapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class DispatchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
        startActivity(new Intent(this, MapDemoActivity.class));
    }
}
