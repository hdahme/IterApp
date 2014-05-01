package com.iterapp.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.iterapp.R;
import com.iterapp.EventsAdapter;
import com.iterapp.MapDemoActivity;
import com.iterapp.models.Event;

public class EventsListFragment extends Fragment {
    private ListView lvEvents;
    private EventsAdapter adapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events_list, parent, false);
        
        ArrayList<Event> events = new ArrayList<Event>();
        this.adapter = new EventsAdapter(view.getContext(), events);
        this.lvEvents = (ListView) view.findViewById(R.id.lvEvents);
        this.lvEvents.setAdapter(this.adapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.getEvents();
    }

    private void getEvents() {
    	EventsListFragment.this.adapter.addAll(MapDemoActivity.eventList);
    }
}
