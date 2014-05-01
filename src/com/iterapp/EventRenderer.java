package com.iterapp;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.iterapp.models.ClusteredEvent;

public class EventRenderer  extends DefaultClusterRenderer<ClusteredEvent>  {

    public EventRenderer(Context context, GoogleMap map,
			ClusterManager<ClusteredEvent> clusterManager) {
		super(context, map, clusterManager);

    }
    
    
    protected void onBeforeClusterItemRendered(ClusteredEvent event, MarkerOptions markerOptions) {
        // Draw a single person.
        // Set the info window to show their name.
        markerOptions = event.getMarkerOptions();
    }

}
