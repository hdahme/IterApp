package com.example.mapdemo;

import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mapdemo.models.Event;
import com.example.mapdemo.models.EventType;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class CreateEventActivity extends Activity {

	private Spinner spinner;
	private EditText eventName; 
	private EditText eventDescription;
	private ImageView submitEvent;
	private Event newEvent;
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
	
		submitEvent = (ImageView)findViewById(R.id.ivStartEvent);
		
		submitEvent.setOnClickListener(new View.OnClickListener(){
		    public void onClick(View v) {   
		    	onCreateNewEvent();
		     }
		});
		
		
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
	
	
	public void onCreateNewEvent(){
		
		// Check to see if necessary data entered
		if(spinner.getSelectedItem().toString().equals("Select Event Type")){
			Toast.makeText(this, "Please enter the type of event", Toast.LENGTH_SHORT).show();
			return;
		}
			
		if(eventName.getText().toString().isEmpty()){
			Toast.makeText(this, "Please enter the event name", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// Create the event object to save
		newEvent = new Event();
		ParseUser currentUser = ParseUser.getCurrentUser();

		newEvent.setDescription(eventDescription.getText().toString());
		newEvent.setTitle(eventName.getText().toString());
		// Map spinner value to internal event type
		EventType eventType = EventType.fromDisplayValue(spinner.getSelectedItem().toString());
		newEvent.setType(eventType);
		newEvent.setActive(true);
		//newEvent.setParticipants(Arrays.asList(currentUser));
		newEvent.addParticipant(currentUser);
		newEvent.setOwner(currentUser);		

		newEvent.saveInBackground(new SaveCallback(){
			@Override
			public void done(ParseException e) {
				Intent data = new Intent();		
				data.putExtra(MapDemoActivity.NEW_EVENT, newEvent.getObjectId());
				setResult(RESULT_OK, data);
				finish();
			}
		});
		
		
	}


}
