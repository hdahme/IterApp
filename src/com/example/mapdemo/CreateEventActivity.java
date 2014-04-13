package com.example.mapdemo;

import com.example.mapdemo.models.Event;
import com.parse.ParseUser;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.os.Build;

public class CreateEventActivity extends Activity {

	Spinner spinner;
	EditText eventName; 
	EditText eventDescription;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_event);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.select_event, android.R.layout.simple_spinner_item);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner = (Spinner) findViewById(R.id.spSelectEvent);
		spinner.setAdapter(adapter);
		
		eventName = (EditText)findViewById(R.id.etEventName);
		eventDescription = (EditText)findViewById(R.id.etDescription);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_event, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	public void onCreateNewEventClick(){
		
		// Check to see if necessary data entered
		if(spinner.getSelectedItem().toString().equals("Select Event Type")){
			Toast.makeText(this, "Please enter the type of event", Toast.LENGTH_SHORT).show();
			return;
		}
			
		if(eventName.getText().toString().isEmpty()){
			Toast.makeText(this, "Please enter the event name", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// Get the username to save with the event
	    SharedPreferences loginPrefs = getSharedPreferences(LoginActivity.LOGIN_PREFS_NAME, 0);
	    String username = loginPrefs.getString("username", "");
	    if(username.equals("")){
			Toast.makeText(this, "Error Loading Username", Toast.LENGTH_SHORT).show();
			return;
	    }
		
		// Create the event object to save
		Event newEvent = new Event();
		ParseUser currentUser = ParseUser.getCurrentUser();

		newEvent.setDescription(eventDescription.getText().toString());
		newEvent.setTitle(eventName.getText().toString());
		newEvent.setType(spinner.getSelectedItem().toString());
		newEvent.setOwner(currentUser);		

		newEvent.saveInBackground();
	}


}