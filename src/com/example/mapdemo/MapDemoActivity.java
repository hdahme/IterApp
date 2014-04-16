package com.example.mapdemo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mapdemo.models.Event;
import com.example.mapdemo.models.LocationUpdate;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class MapDemoActivity extends FragmentActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	private SupportMapFragment mapFragment;
	private GoogleMap map;
	private LocationClient mLocationClient;
	// Only makes sense to fetch as often as they're sent
	private int fetchEventInterval = (int)GPSTracking.MIN_TIME_BW_UPDATES; 
	private int sendLocationInterval = (int)GPSTracking.MIN_TIME_BW_UPDATES;
	private int amountOfHistoryToPull = 1000 * 60 * 60 * 24; // 1 day
	private Handler fetchEventHandler;
	private Handler sendLocationHandler;
	private Event currentEvent;
	private String currentUser;
	private AlertDialog.Builder dialogBuilder;
	private EventFilters eventFilters = null;
	private static final int FILTERS_REQUEST_CODE = 1;
	
	public static final int NEW_EVENT_CODE = 100;
	public static final String HIKE_KEY = "hike";
	public static final String BIKE_KEY = "bike";
	public static final String BAR_CRAWL_KEY = "bar_crawl";
	public static final String EVENT = "event";
	public static final String CURRENT_USER = "current user";
	public static final String NEW_EVENT = "new event";
	
	public static final Map<String, Integer> eventTypeMap = new HashMap<String, Integer>();
	public static final Map<String, String> coloquialTypeName = new HashMap<String, String>();
	public static List<Event> eventList;
	public static List<LocationUpdate> eventLocations;

	GPSTracking gps;
	
	/*
	 * Define a request code to send to Google Play services This code is
	 * returned in Activity.onActivityResult
	 */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_demo_activity);
		
		// Initialize the mapping of event type to drawable resource
		if (MapDemoActivity.eventTypeMap.size() == 0) {
			MapDemoActivity.eventTypeMap.put(BIKE_KEY, R.drawable.ic_bike);
			MapDemoActivity.eventTypeMap.put(HIKE_KEY, R.drawable.ic_hike);
			MapDemoActivity.eventTypeMap.put(BAR_CRAWL_KEY, R.drawable.ic_beer);
		}
		
		// Initialize the mapping of event type to drawable resource
		if (MapDemoActivity.coloquialTypeName.size() == 0) {
			String[] strArray = getResources().getStringArray(R.array.select_event);
			MapDemoActivity.coloquialTypeName.put(strArray[1], BIKE_KEY);
			MapDemoActivity.coloquialTypeName.put(strArray[2], HIKE_KEY);
			MapDemoActivity.coloquialTypeName.put(strArray[3], BAR_CRAWL_KEY);
		}
		currentUser = (String) getIntent().getSerializableExtra(CURRENT_USER);
		dialogBuilder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
		
		mLocationClient = new LocationClient(this, this, this);
		mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
		if (mapFragment != null) {
			map = mapFragment.getMap();
			map.setOnMarkerClickListener(new OnMarkerClickListener() {
				public boolean onMarkerClick(Marker marker) {
					ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
					query.whereEqualTo("objectId", marker.getTitle());
			        query.findInBackground(new FindCallback<Event>() {
			        	private Event event;
						public void done(List<Event> events, ParseException e) {
							event = events.get(0);
							dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
								}
							});
							
							if (currentEvent == null) {
								dialogBuilder.setPositiveButton(R.string.join, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										Toast.makeText(getBaseContext(), "Joining event", Toast.LENGTH_SHORT).show();
										currentEvent = event;
									}
								});
							} else if (currentEvent.getOwner().getObjectId().equals(currentUser)) {
								dialogBuilder.setPositiveButton(R.string.end_event, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										Toast.makeText(getBaseContext(), "Ending event", Toast.LENGTH_SHORT).show();
										currentEvent = null;
										stopSendingLocation();
									}
								});
							} else {
								dialogBuilder.setPositiveButton(R.string.leave, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										Toast.makeText(getBaseContext(), "Leaving event", Toast.LENGTH_SHORT).show();
										currentEvent = null;
									}
								});
							}
							dialogBuilder.setTitle(event.getTitle())
								.setMessage(event.getDescription());
							AlertDialog d = dialogBuilder.create();
							d.show();
						}
			        });
					return true;
				}
			});
			
			if (map != null) {
				Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
				map.setMyLocationEnabled(true);
			} else {
				Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
		}
		fetchEventData();
		fetchEventHandler = new Handler();
		sendLocationHandler = new Handler();
		

	}
	
	Runnable fetchEventLocations = new Runnable() {
		public void run() {
			fetchEventLocations();
			fetchEventHandler.postDelayed(fetchEventLocations, fetchEventInterval);
		}
	};
	
	Runnable sendLocation = new Runnable() {
		public void run() {
			sendLocation();
			sendLocationHandler.postDelayed(sendLocation, sendLocationInterval);
		}
	};
	
	private void startFetchingEventLocations() {
		fetchEventLocations.run();
	}
	
	private void startSendingLocation() {
		gps = new GPSTracking(MapDemoActivity.this);
		sendLocation.run();
	}
	
	private void stopFetchingEventLocations() {		
		fetchEventHandler.removeCallbacks(fetchEventLocations);
	}
	
	private void stopSendingLocation() {
		gps.stopUsingGPS();
		sendLocationHandler.removeCallbacks(sendLocation);
	}
	
	public void fetchEventData() {
		ParseQuery<Event> query = ParseQuery.getQuery(Event.class);

		if (this.eventFilters != null) {
		    this.eventFilters.applyFiltersToQuery(query);
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
	
	public void sendLocation() {

		double latitude = -1;
		double longitude = -1;
        // check if GPS enabled     
        if(gps.canGetLocation()){
             
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
             
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_SHORT).show();    
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
		query.orderByDescending("timestamp");
		query.whereGreaterThan("timestamp", System.currentTimeMillis() - amountOfHistoryToPull);
        query.findInBackground(new FindCallback<LocationUpdate>() {
            public void done(List<LocationUpdate> itemList, ParseException e) {
                if (e == null) {
                	Collections.sort(itemList);
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
		
		for (int i = 0; i < MapDemoActivity.eventLocations.size(); i++) {
			LocationUpdate l = MapDemoActivity.eventLocations.get(i);
			LocationUpdate otherL = null;
			
			if (i > 0) {
				otherL = MapDemoActivity.eventLocations.get(i-1);
			}
			// If it's the most recent update in a series of updates for a particular event, draw the icon
			if (otherL == null || !l.getEvent().getObjectId().equals(otherL.getEvent().getObjectId())) {
				Marker mapMarker = map.addMarker(new MarkerOptions()
    		    .position(new LatLng(l.getLat(), l.getLng()))                                                      
    		    .title(l.getEvent().getObjectId())
    		    .icon(BitmapDescriptorFactory.fromResource(
    		    		MapDemoActivity.eventTypeMap.get(l.getType()))));
				decayAmount = 0;
			// Otherwise draw polylines connecting the previous location updates
			} else {
				int c = Color.argb(Math.max(255-(decayAmount*75), 0), 0, 0, 0);				
				Polyline polyline = map.addPolyline(new PolylineOptions()
				.add(new LatLng(l.getLat(), l.getLng()), 
				     new LatLng(otherL.getLat(), otherL.getLng()))
				.width(5)
				.color(c));
				
				decayAmount++;
			}
		}
		
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
					startSendingLocation();
				}
	        });
		} else if (resultCode == RESULT_OK && requestCode == MapDemoActivity.FILTERS_REQUEST_CODE) {
		    this.eventFilters = (EventFilters) data.getExtras().getSerializable(EventFilters.EXTRAS_KEY);
		    this.fetchEventData();
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
			Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
			LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
			map.animateCamera(cameraUpdate);
		} else {
			Toast.makeText(this, "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
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

}