package com.example.mapdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.mapdemo.models.EventType;

public class FilterActivity extends Activity {
    private Spinner eventTypeSpinner;

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
    }

    public void onSave(View v) {
        String eventTypeValue = this.eventTypeSpinner.getSelectedItem().toString();
        if (!eventTypeValue.equals("Select Event Type")) {
            this.eventFilters.type = EventType.fromDisplayValue(eventTypeValue).getValue();
        } else {
            this.eventFilters.type = null;
        }

        Intent data = new Intent();
        data.putExtra(EventFilters.EXTRAS_KEY, this.eventFilters);
        setResult(RESULT_OK, data);
        finish();
    }
}
