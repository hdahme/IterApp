package com.example.mapdemo;

import android.app.Application;
import android.util.Log;

import com.example.mapdemo.models.Event;
import com.example.mapdemo.models.LocationUpdate;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

public class IterApplication extends Application {
    private static final String APPLICATION_ID = "3BFzwUwXukcS8oIiNwiUwCyHtxVkaxQoXFrjZl9m";
    private static final String CLIENT_KEY = "5cg4hpkHCHECqHfbIBwBItQvZjUPoxzmwBgmPQk1";
    private static final String FACEBOOK_APP_ID = "1437170006526752";

	@Override
	public void onCreate() {
		super.onCreate();

		ParseObject.registerSubclass(Event.class);
		ParseObject.registerSubclass(LocationUpdate.class);
		Parse.initialize(this, IterApplication.APPLICATION_ID, IterApplication.CLIENT_KEY);
		ParseFacebookUtils.initialize(FACEBOOK_APP_ID);

		ParseACL defaultACL = new ParseACL();
		defaultACL.setPublicReadAccess(true);
		defaultACL.setPublicWriteAccess(true);

		ParseACL.setDefaultACL(defaultACL, true);
	}
}
