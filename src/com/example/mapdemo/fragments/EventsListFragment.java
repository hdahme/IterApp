package com.example.mapdemo.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.mapdemo.EventsAdapter;
import com.example.mapdemo.R;
import com.example.mapdemo.models.Event;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

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
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.findInBackground(new FindCallback<Event>() {
            public void done(List<Event> itemList, ParseException e) {
                if (e == null) {
                    EventsListFragment.this.adapter.addAll(itemList);
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }
}
