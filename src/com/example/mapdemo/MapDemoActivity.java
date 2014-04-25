package com.example.mapdemo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapdemo.helpers.MapBoxTileProvider;
import com.example.mapdemo.models.ClusteredEvent;
import com.example.mapdemo.models.Event;
import com.example.mapdemo.models.LocationUpdate;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.internal.in;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.loopj.android.image.SmartImageView;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.slidinglayer.SlidingLayer;

public class MapDemoActivity extends FragmentActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		GPSListenerUpdate{

	private SupportMapFragment mapFragment;
	private GoogleMap map;
	private LocationClient mLocationClient;
	private SlidingLayer slidingLayer;
	private Button positiveButton;
	private Button negativeButton;
	private TextView slideEventTitle;
	private TextView slideEventDescription;
	private TextView slideHost;
	private TextView slideAttendeeCount;
	private TextView eventInProgress;
	private TextView notificationArea;
	
	// Only makes sense to fetch as often as they're sent
	private int fetchEventInterval = (int)GPSTracking.MIN_TIME_BW_UPDATES; 
	private int amountOfHistoryToPull = 1000 * 60 * 60 * 24; // 1 day, should be = fetchEventInterval
	private int attendanceChangeInterval = 5000;
	private Handler fetchEventHandler;
	private Handler attenendanceChangeHandler;
	private Event currentEvent;
	private Event temporaryEvent;
	private ParseUser temporaryUser;
	private ParseUser currentUser;
	private EventFilters eventFilters = null;
	private static final int FILTERS_REQUEST_CODE = 1;
	
	public static final int NEW_EVENT_CODE = 100;
	public static final int FACEBOOK_LOGIN = 314;
	public static final int NOTIFICATION_ID = 1;
	public static final String EVENT = "event";
	public static final String NEW_EVENT = "new event";
	public static final String ITERAPP_TILE_PROVIDER = "hdahme.i29l01e4";

	public static List<Event> eventList;
	public static List<LocationUpdate> eventLocations;
	private ArrayList<Polyline> polylines = new ArrayList<Polyline>();
	private ArrayList<Marker> markers = new ArrayList<Marker>();
	private ArrayList<MarkerOptions> markerOptions = new ArrayList<MarkerOptions>();
	private ArrayList<PolylineOptions> polyLineOptions = new ArrayList<PolylineOptions>();

	GPSTracking gps;
    // Declare a variable for the cluster manager.
    private ClusterManager<ClusteredEvent> mClusterManager;
	
	/*
	 * Define a request code to send to Google Play services This code is
	 * returned in Activity.onActivityResult
	 */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_demo_activity);
		
		ParseFacebookUtils.logIn(this, FACEBOOK_LOGIN, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				if (err != null) {
					Log.d("fbId", err.getMessage());
				}
				if (user == null) {
					Log.d("fbId", "Uh oh. The user cancelled the Facebook login.");
					Toast.makeText(getBaseContext(), "Please log in with Facebook", Toast.LENGTH_SHORT).show();
			    } else if (user.isNew()) {
			    	Log.d("fbId", "User signed up and logged in through Facebook!");
			    	getFacebookIdInBackground();
			    } else {
			    	Log.d("fbId", "User logged in through Facebook!");
			    	getFacebookIdInBackground();
			    }
			}
		});

		bindViews();
		Log.d("fbId", "launched from notif");
		
		currentUser = ParseUser.getCurrentUser();
		try {
			Log.d("fbId", currentUser.getObjectId());
			String currentEventIdOnLoad = currentUser.getString("currentEvent");
			ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
			query.whereEqualTo("objectId", currentEventIdOnLoad);
			query.findInBackground(new FindCallback<Event>() {
				public void done(List<Event> events, ParseException e) {
					try {
						currentEvent = events.get(0);
						Log.d("fbId", currentEvent.getObjectId());
						temporaryUser = currentUser;
						showEventInPogress();
					} catch (Exception ex) {
						currentEvent = null;
					}
				}
			});
		} catch (Exception ex) {
			currentEvent = null;
		}
		
		if (mapFragment != null) {
			map = mapFragment.getMap();
			map.setOnMarkerClickListener(new OnMarkerClickListener() {
				public boolean onMarkerClick(Marker marker) {
					ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
					query.whereEqualTo("objectId", marker.getTitle());
			        query.findInBackground(new FindCallback<Event>() {
						public void done(List<Event> events, ParseException e) {
							temporaryEvent = events.get(0);
							
							// Once we know the event we've clicked, query again to get information about the host
							// Ideally this could be extended to include profile photos, etc
							ParseQuery<ParseUser> query = ParseUser.getQuery();
							query.whereEqualTo("objectId", temporaryEvent.getOwner().getObjectId());
					        query.findInBackground(new FindCallback<ParseUser>() {
								public void done(List<ParseUser> hosts, ParseException e) {
									temporaryUser = hosts.get(0);
									
									if (!slidingLayer.isOpened()) {
										populateSlider();
										slidingLayer.openLayer(true);
						            }
								}
					        });
						}
			        });
					return true;
				}
			});
			
			if (map != null) {
				//Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
				map.setMyLocationEnabled(true);
			} else {
				Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
		}
		fetchEventData();
		fetchEventHandler = new Handler();
		attenendanceChangeHandler = new Handler();
		
		// Set up custom tile provider
		TileOverlayOptions opts = new TileOverlayOptions();
		opts.tileProvider(new MapBoxTileProvider(ITERAPP_TILE_PROVIDER));
		map.addTileOverlay(opts);
		// Turn off Google's tiles
		map.setMapType(GoogleMap.MAP_TYPE_NONE);
		
		// Send GPS location initially
		sendLocationData();
		///setUpClusterer();
	
	}

	private void sendLocationData() {
		gps = new GPSTracking(MapDemoActivity.this);	
	}

	public void bindViews() {
		mLocationClient = new LocationClient(this, this, this);
		mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
		
		positiveButton = (Button)findViewById(R.id.btnPositive);
		negativeButton = (Button)findViewById(R.id.btnNegative);
		slideEventTitle = (TextView)findViewById(R.id.tvSlideEventTitle);
		slideEventDescription = (TextView)findViewById(R.id.tvSlideEventDescription);
		slideHost = (TextView)findViewById(R.id.tvSlideHost);
		slideAttendeeCount = (TextView)findViewById(R.id.tvSlideAttendeeCount);
		eventInProgress = (TextView)findViewById(R.id.tvEventInProgress);
		eventInProgress.setAlpha(0f);
		notificationArea = (TextView)findViewById(R.id.tvNotif);
		notificationArea.setAlpha(0f);
		
		// Draw the sliding panel at the bottom of the map
		slidingLayer = (SlidingLayer) findViewById(R.id.slidingLayer1);

		slidingLayer.setShadowWidthRes(R.dimen.shadow_width);
		slidingLayer.setOffsetWidth(25);
		slidingLayer.setShadowDrawable(R.drawable.sidebar_shadow);
		slidingLayer.setStickTo(SlidingLayer.STICK_TO_BOTTOM);
		slidingLayer.setCloseOnTapEnabled(false);
	}
	
	public void populateSlider() {
		if (currentEvent == null && temporaryEvent.isActive()) {
			positiveButton.setText(R.string.join);
		} else if (currentEvent == null) {
			positiveButton.setText(R.string.become_host);
		} else if (((String)temporaryEvent.getOwner().getObjectId()).equals(currentUser.getObjectId())) {
			positiveButton.setText(R.string.end_event);
		} else if (temporaryEvent != null){
			positiveButton.setText(R.string.leave);
		} else {
			positiveButton.setText("=)");
		}
		
		negativeButton.setText(R.string.cancel);
		slideAttendeeCount.setText(temporaryEvent.getNumberOfParticipants() + " Attendees");
		slideHost.setText(temporaryUser.getString("firstName")+" "+temporaryUser.getString("lastName"));
		slideEventDescription.setText(temporaryEvent.getDescription());
		slideEventTitle.setText(temporaryEvent.getTitle());
		
	}
	
	public void onPositiveButtonPress(View v) {
		String tempEventOwnerId = (String)temporaryEvent.getOwner().getObjectId();
		if (currentEvent == null) {
			
			// So that people have a chance to let an event continue, if the host leaves
			if (!temporaryEvent.isActive()) {
				temporaryEvent.setActive(true);
				temporaryEvent.setOwner(currentUser);
				startFetchingAttendanceChanges();
				Toast.makeText(getBaseContext(), "Becoming Host", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getBaseContext(), "Joining event", Toast.LENGTH_SHORT).show();
			}
			temporaryEvent.addParticipant(currentUser);
			temporaryEvent.saveInBackground(new SaveCallback(){
				public void done(ParseException e) { fetchEventLocations(); }
			});
			currentEvent = temporaryEvent;
			showEventInPogress();
			currentUser.put("currentEvent", temporaryEvent.getObjectId());
			currentUser.saveInBackground();
			
		} else if (tempEventOwnerId.equals(currentUser.getObjectId())) {
			Toast.makeText(getBaseContext(), "Ending event", Toast.LENGTH_SHORT).show();
			temporaryEvent.setActive(false);
			temporaryEvent.removeParticipant(currentUser);
			temporaryEvent.saveInBackground(new SaveCallback(){
				public void done(ParseException e) {fetchEventLocations();}
			});
			currentEvent = null;
			hideEventInProgress();
			stopFetchingAttendanceChanges();
			ParseUser.getCurrentUser().put("currentEvent", "");
	        ParseUser.getCurrentUser().saveInBackground();
			stopSendingLocation();
			
		} else if (temporaryEvent != null){
			Toast.makeText(getBaseContext(), "Leaving event", Toast.LENGTH_SHORT).show();
			temporaryEvent.removeParticipant(currentUser);
			temporaryEvent.saveInBackground(new SaveCallback(){
				public void done(ParseException e) {fetchEventLocations();}
			});
			currentEvent = null;
			hideEventInProgress();
			ParseUser.getCurrentUser().put("currentEvent", "");
	        ParseUser.getCurrentUser().saveInBackground();
	        
		}
		
		if (slidingLayer.isOpened()) {
			temporaryEvent = null;
			slidingLayer.closeLayer(true);
        }
	}
	
	private void showEventInPogress() {
		eventInProgress.setText(R.string.event_in_progress);
		eventInProgress.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				temporaryEvent = currentEvent;
				ParseQuery<ParseUser> query = ParseUser.getQuery();
				query.whereEqualTo("objectId", temporaryEvent.getOwner().getObjectId());
		        query.findInBackground(new FindCallback<ParseUser>() {
					public void done(List<ParseUser> hosts, ParseException e) {
						temporaryUser = hosts.get(0);
						if (!slidingLayer.isOpened()) {
							populateSlider();
							ObjectAnimator fadeInAnim = ObjectAnimator.ofFloat(notificationArea, "alpha", 
									notificationArea.getAlpha(), 0f);
							notificationArea.setText("0");
							fadeInAnim.start();
							slidingLayer.openLayer(true);
			            }
					};
		        });
			}
		});
		ObjectAnimator fadeInAnim = ObjectAnimator.ofFloat(eventInProgress, "alpha", 0f, 1f);
		fadeInAnim.start();
	}
	
	private void hideEventInProgress() {
		eventInProgress.setText("");
		eventInProgress.setOnClickListener(new OnClickListener() {	public void onClick(View v) {}});
		ObjectAnimator fadeInAnim = ObjectAnimator.ofFloat(eventInProgress, "alpha", 1f, 0f);
		fadeInAnim.start();
	}
	
	private void stopSendingLocation() {
		if(gps != null)
			gps.stopUsingGPS();		
	}

	public void onNegativeButtonPress(View v) {
		if (slidingLayer.isOpened()) {
			temporaryEvent = null;
			slidingLayer.closeLayer(true);
        }
	}
	
	Runnable fetchEventLocations = new Runnable() {
		public void run() {
			fetchEventLocations();
			fetchEventHandler.postDelayed(fetchEventLocations, fetchEventInterval);
		}
	};
	
	private void startFetchingEventLocations() {
		fetchEventLocations.run();
	}
	
	private void stopFetchingEventLocations() {		
		fetchEventHandler.removeCallbacks(fetchEventLocations);
	}
	
	Runnable fetchAttendanceChange = new Runnable() {
		public void run() {
			fetchAttendanceChanges();
			attenendanceChangeHandler.postDelayed(fetchAttendanceChange, attendanceChangeInterval);
		}
	};
	
	private void startFetchingAttendanceChanges() {
		fetchAttendanceChange.run();
	}
	
	private void stopFetchingAttendanceChanges() {		
		attenendanceChangeHandler.removeCallbacks(fetchAttendanceChange);
	}
	
	public void fetchEventData() {
		ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
		
		// Only pull active events
		query.whereEqualTo("active", true);

        if (this.eventFilters != null) {
            if (gps != null && gps.canGetLocation()) {
                this.eventFilters.setCurrentLocation(gps.getLatitude(), gps.getLongitude());
            }
            this.eventFilters.applyFiltersToEventQuery(query);
        }

        query.findInBackground(new FindCallback<Event>() {
            public void done(List<Event> itemList, ParseException e) {
                if (e == null) {
                    MapDemoActivity.eventList = itemList;
                    // Only start fetching event locations once we've got the list of events. Avoids NullPointer
                    startFetchingEventLocations();
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
	}
	
	// Refresh the current event, looking for attendance changes. 
	public void fetchAttendanceChanges() {
		Log.d("fbId", "fetching attendees");
		final ArrayList<String> oldParticipants = currentEvent.getParticipants();
		ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
		query.whereEqualTo("objectId", currentEvent.getObjectId());
		query.findInBackground(new FindCallback<Event>() {
			public void done(List<Event> events, ParseException e) {
				currentEvent = events.get(0);
				
				// No changes
				ArrayList<String> newParticipants = currentEvent.getParticipants();
				if (oldParticipants.equals(newParticipants)) {
					return;
				}
				
				// oldParticipants is now everyone who has left between updates, 
				// newParticipants is now everyone who has joined between updates
				ArrayList<String> oP = new ArrayList<String>(oldParticipants); 
				oldParticipants.removeAll(newParticipants);
				newParticipants.removeAll(oP);
				
				// Notify the host/current user
				Vibrator v = (Vibrator) getBaseContext().getSystemService(getBaseContext().VIBRATOR_SERVICE);
				v.vibrate(500);
				Log.d("fbId", "Leaving members");
				Log.d("fbId", oldParticipants.toString());
				Log.d("fbId", "Joining members");
				Log.d("fbId", newParticipants.toString());
				
				int notifAttendees = 0;
				if(!notificationArea.getText().toString().equals("")) {
					notifAttendees = Integer.parseInt(notificationArea.getText().toString());
				}
				
				// In app notifications
				notifAttendees = notifAttendees + newParticipants.size() - oldParticipants.size();
				if (notifAttendees != 0) {
					notificationArea.setText(String.valueOf(notifAttendees));
					ObjectAnimator fadeInAnim = ObjectAnimator.ofFloat(notificationArea, "alpha", 
							notificationArea.getAlpha(), 1f);
					fadeInAnim.start();
				}
				ParseQuery<ParseUser> query = ParseUser.getQuery();
				query.whereContainedIn("objectId", newParticipants);
		        query.findInBackground(new FindCallback<ParseUser>() {
					public void done(List<ParseUser> newParticipants, ParseException e) {
						ArrayList<String> newAttendeeNames = new ArrayList<String>();
						for (ParseUser u : newParticipants){
							newAttendeeNames.add(u.getString("name"));
						}
						
						String contentTitle = newParticipants.size() > 1 ? "Attendees!" : "Attendee!";
						String contentBody = newAttendeeNames.get(0);
						if (newParticipants.size() > 1) {
							contentBody += " and " + String.valueOf(newParticipants.size() - 1) + " other";
							contentBody = newParticipants.size() > 2 ? contentBody+"s" : contentBody;
						}
						contentBody += " joined your event!";
						
						// Task bar notification
						NotificationCompat.Builder mBuilder =
						        new NotificationCompat.Builder(getBaseContext())
						        .setSmallIcon(R.drawable.ic_launcher)
						        .setContentTitle(newParticipants.size() + " New Event " + contentTitle)
						        .setContentText(contentBody)
						        .setAutoCancel(true);
						Intent notifIntent = new Intent(getBaseContext(), MapDemoActivity.class);
						TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
						stackBuilder.addParentStack(MapDemoActivity.class);
						stackBuilder.addNextIntent(notifIntent);
						PendingIntent resultPendingIntent =
						        stackBuilder.getPendingIntent(
						            0,
						            PendingIntent.FLAG_UPDATE_CURRENT
						        );
						mBuilder.setContentIntent(resultPendingIntent);
						NotificationManager mNotificationManager =
						    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
						mNotificationManager.notify(MapDemoActivity.NOTIFICATION_ID, mBuilder.build());
					};
		        });
				
				// Refresh the sliding panel, if it's open
				if (slidingLayer.isOpened()) {
					temporaryEvent = currentEvent;
					temporaryUser = currentUser;
					populateSlider();
		        }
			}
		});
	}
	
	public void sendLocation() {

		double latitude = 0;
		double longitude = 0;
        // check if GPS enabled     
        if(gps.canGetLocation()){
             
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
        
		LocationUpdate locationUpdate = new LocationUpdate();
		ParseUser currentUser = ParseUser.getCurrentUser();
		locationUpdate.setLat(latitude);
		locationUpdate.setLng(longitude);
		locationUpdate.setType(currentEvent.getType());
		locationUpdate.setEvent(currentEvent);
		locationUpdate.setUser(currentUser);
		locationUpdate.setTimestamp(System.currentTimeMillis());
		locationUpdate.saveInBackground();
	}
	
	public void fetchEventLocations() {
		ParseQuery<LocationUpdate> query = ParseQuery.getQuery(LocationUpdate.class);
        if (this.eventFilters != null) {
            if (gps != null && gps.canGetLocation()) {
                this.eventFilters.setCurrentLocation(gps.getLatitude(), gps.getLongitude());
            }
            this.eventFilters.applyFiltersToLocationUpdateQuery(query);
        }
		query.orderByDescending("timestamp");
		query.whereGreaterThan("timestamp", System.currentTimeMillis() - amountOfHistoryToPull);
        query.findInBackground(new FindCallback<LocationUpdate>() {
            public void done(List<LocationUpdate> itemList, ParseException e) {
                if (e == null) {
                    if (!itemList.isEmpty()) {
                        Collections.sort(itemList);
                    }
                    MapDemoActivity.eventLocations = itemList;
                    renderEventHistoryAndIcons();
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
	}
	
	public void renderEventHistoryAndIcons() {
		int decayAmount = 0;
		
		// Wipe all old icons, polylines, etc - map.clear removes custom tile overlay
   		for (Polyline line : polylines){
   			line.remove();
   		}
   		polylines.clear();
   		for (Marker marker : markers){
   			marker.remove();
   		}
   		markers.clear();
   		polyLineOptions.clear();
   		markerOptions.clear();
		
		for (int i = 0; i < MapDemoActivity.eventLocations.size(); i++) {
			LocationUpdate l = MapDemoActivity.eventLocations.get(i);
			LocationUpdate otherL = null;
			
			if (i > 0) {
				otherL = MapDemoActivity.eventLocations.get(i-1);
			}
			// If it's the most recent update in a series of updates for a particular event, draw the icon
			if (otherL == null || !l.getEvent().getObjectId().equals(otherL.getEvent().getObjectId())) {
				markerOptions.add(new MarkerOptions()
    		    .position(new LatLng(l.getLat(), l.getLng()))                                                      
    		    .title(l.getEvent().getObjectId())
    		    .icon(BitmapDescriptorFactory.fromResource(l.getTypeObject().getDrawableId())));
				decayAmount = 0;
			// Otherwise draw polylines connecting the previous location updates
			} else {
				int c = Color.argb(Math.max(255-(decayAmount*5), 0), 187, 59, 51);				
				Polyline polyline = map.addPolyline(new PolylineOptions()
				.add(new LatLng(l.getLat(), l.getLng()), 
				     new LatLng(otherL.getLat(), otherL.getLng()))
				.width(7)
				.color(c));
				
				// Draw it above the custom overlay
				polyline.setZIndex(1000);
				polylines.add(polyline);
				
				decayAmount++;
			}
		}
		
		for(int i = 0; i < markerOptions.size(); i++){
			
			Marker mapMarker = map.addMarker(markerOptions.get(i));
			markers.add(mapMarker);
			
		}
		
		//setUpClusterer(markers, markerOptions);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getMenuInflater().inflate(R.menu.map_demo, menu);
		//getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		//getActionBar().setCustomView(R.layout.action_bar);
		return true;
	}
	
	public void onAddEventClick(MenuItem mi) {
		Intent i = new Intent(MapDemoActivity.this, CreateEventActivity.class);
        startActivityForResult(i, NEW_EVENT_CODE);
	}

	public void onSettingsClick(MenuItem mi) {
	    Intent i = new Intent(this, SettingsActivity.class);
	    startActivity(i);
	}

	public void onFilterClick(MenuItem mi) {
	    Intent i = new Intent(this, FilterActivity.class);
	    i.putExtra(EventFilters.EXTRAS_KEY, this.eventFilters);
	    startActivityForResult(i, MapDemoActivity.FILTERS_REQUEST_CODE);
	}

	/*
	 * Called when the Activity becomes visible.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		if (isGooglePlayServicesAvailable()) {
			mLocationClient.connect();
		}

	}

	/*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		mLocationClient.disconnect();
		super.onStop();
	}

	/*
	 * Handle results returned to the FragmentActivity by Google Play services
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == NEW_EVENT_CODE) {
			String objId = (String)data.getExtras().getSerializable(NEW_EVENT);
		    // Query Parse for the referenced event
			ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
			query.whereEqualTo("objectId", objId);
	        query.findInBackground(new FindCallback<Event>() {
				@Override
				public void done(List<Event> events, ParseException e) {
					currentEvent = events.get(0);
					gps = new GPSTracking(MapDemoActivity.this);
					gps.addGPSUpdateListener(MapDemoActivity.this);
					fetchEventLocations();
					showEventInPogress();
					startFetchingAttendanceChanges();
				}
	        });
		} else if (resultCode == RESULT_OK && requestCode == MapDemoActivity.FILTERS_REQUEST_CODE) {
		    this.eventFilters = (EventFilters) data.getExtras().getSerializable(EventFilters.EXTRAS_KEY);
		    this.fetchEventData();
		} else if (requestCode == MapDemoActivity.FACEBOOK_LOGIN) {
			super.onActivityResult(requestCode, resultCode, data);
			ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
		}

		// Decide what to do based on the original request code
		switch (requestCode) {

		case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
			switch (resultCode) {
			case Activity.RESULT_OK:
				mLocationClient.connect();
				break;
			}

		}
	}

	private boolean isGooglePlayServicesAvailable() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Location Updates", "Google Play services is available.");
			return true;
		} else {
			// Get the error dialog from Google Play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this,
					CONNECTION_FAILURE_RESOLUTION_REQUEST);

			// If Google Play services can provide an error dialog
			if (errorDialog != null) {
				// Create a new DialogFragment for the error dialog
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				errorFragment.setDialog(errorDialog);
				errorFragment.show(getSupportFragmentManager(), "Location Updates");
			}

			return false;
		}
	}

	/*
	 * Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request the current
	 * location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle dataBundle) {
		// Display the connection status
		Location location = mLocationClient.getLastLocation();
		if (location != null) {
			//Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
			LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
			map.animateCamera(cameraUpdate);
		} else {
			Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * Called by Location Services if the connection to the location client
	 * drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
	}

	/*
	 * Called by Location Services if the attempt to Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"Sorry. Location services not available to you", Toast.LENGTH_SHORT).show();
		}
	}

	// Define a DialogFragment that displays the error dialog
	public static class ErrorDialogFragment extends DialogFragment {

		// Global field to contain the error dialog
		private Dialog mDialog;

		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		// Set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_BACK:
            if (slidingLayer.isOpened()) {
            	slidingLayer.closeLayer(true);
                return true;
            }

        default:
            return super.onKeyDown(keyCode, event);
        }
    }

	private void getFacebookIdInBackground() {
		Request.newMeRequest(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (user != null) {
			        ParseUser.getCurrentUser().put("fbId", user.getId());
			        ParseUser.getCurrentUser().put("name", user.getName());
			        ParseUser.getCurrentUser().put("firstName", user.getFirstName());
			        ParseUser.getCurrentUser().put("lastName", user.getLastName());
			        ParseUser.getCurrentUser().put("profilePhoto", "http://graph.facebook.com/"+user.getId()+"/picture?type=small");
			        ParseUser.getCurrentUser().saveInBackground();
			        Log.d("fbId", user.getId());
			      }
			}
		  }).executeAsync();
		}

	@Override
	public void ReceiveGPSData() {
		sendLocation();
	}
	
	
	
	// Set up clustering functionality
	private void setUpClusterer(ArrayList<Marker> markers, ArrayList<MarkerOptions> opts) {


	    // Position the map.
		//map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));

	    // Initialize the manager with the context and the map.
	    mClusterManager = new ClusterManager<ClusteredEvent>(this, mapFragment.getMap());
	    mClusterManager.setRenderer(new EventRenderer(this, map, mClusterManager));

	    // Point the map's listeners at the listeners implemented by the cluster
	    // manager.
	    map.setOnCameraChangeListener(mClusterManager);
	    map.setOnMarkerClickListener(mClusterManager);

	    // Add cluster items (markers) to the cluster manager.
	    addItems(markers, opts);
	}

	private void addItems(ArrayList<Marker> markers, ArrayList<MarkerOptions> opts) {

		if(markers== null)
			return;
		
	    for (int i = 0; i < markers.size(); i++) {
	       
	    	ClusteredEvent itm = new ClusteredEvent();
	        
	    	LatLng coOrd = markers.get(i).getPosition();
	    	itm.setMarker(coOrd);
	    	itm.setMarkerOptions(opts.get(i));
	        mClusterManager.addItem(itm);
	    }
	    int j = 0;
	}
}