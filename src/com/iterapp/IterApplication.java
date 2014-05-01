package com.iterapp;

import android.app.Application;

import com.iterapp.models.Event;
import com.iterapp.models.LocationUpdate;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.PushService;

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
		PushService.setDefaultPushCallback(this, MapDemoActivity.class);

		ParseACL defaultACL = new ParseACL();
		defaultACL.setPublicReadAccess(true);
		defaultACL.setPublicWriteAccess(true);

		ParseACL.setDefaultACL(defaultACL, true);
	}
}
