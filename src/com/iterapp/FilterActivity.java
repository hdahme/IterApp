package com.iterapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.iterapp.R;
import com.iterapp.models.EventType;

public class FilterActivity extends Activity {
    private Spinner eventTypeSpinner;
    private Spinner distanceSpinner;

    private EventFilters eventFilters = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        this.eventFilters = (EventFilters) getIntent().getSerializableExtra(EventFilters.EXTRAS_KEY);
        if (this.eventFilters == null) {
            this.eventFilters = new EventFilters();
        }
        
        this.setupViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter, menu);
        return true;
    }

    public void setupViews() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.select_event, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.eventTypeSpinner = (Spinner) findViewById(R.id.spEventType);
        this.eventTypeSpinner.setAdapter(adapter);

        // populate the spinner with the current setting for type, if any
        if (this.eventFilters.type != null) {
            EventType eventType = EventType.fromValue(this.eventFilters.type);
            if (eventType != null) {
                int position = adapter.getPosition(eventType.getDisplayValue());
                this.eventTypeSpinner.setSelection(position);
            }
        }

        ArrayList<CharSequence> distances = new ArrayList<CharSequence>();
        distances.add("any");
        distances.add("1");
        distances.add("5");
        distances.add("10");
        distances.add("25");
        distances.add("50");

        adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, distances);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.distanceSpinner = (Spinner) findViewById(R.id.spDistance);
        this.distanceSpinner.setAdapter(adapter);
        
        // populate the spinner with the current setting for distance, if any
        if (this.eventFilters.maxDistance > 0) {
            String maxDistanceString = String.valueOf((int)this.eventFilters.maxDistance);
            int position = distances.indexOf(maxDistanceString);
            if (position > -1) {
                this.distanceSpinner.setSelection(position);
            }
        }
    }

    public void onSave(View v) {
        String eventTypeValue = this.eventTypeSpinner.getSelectedItem().toString();
        if (!eventTypeValue.equals("Select Event Type")) {
            this.eventFilters.type = EventType.fromDisplayValue(eventTypeValue).getValue();
        } else {
            this.eventFilters.type = null;
        }

        String distanceValue = this.distanceSpinner.getSelectedItem().toString();
        if (!distanceValue.equals("any")) {
            this.eventFilters.maxDistance = Double.parseDouble(distanceValue);
        } else {
            this.eventFilters.maxDistance = 0;
        }

        Intent data = new Intent();
        data.putExtra(EventFilters.EXTRAS_KEY, this.eventFilters);
        setResult(RESULT_OK, data);
        finish();
    }
}
