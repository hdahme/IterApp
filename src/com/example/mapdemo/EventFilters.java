package com.example.mapdemo;

import java.io.Serializable;

import com.example.mapdemo.models.Event;
import com.example.mapdemo.models.LocationUpdate;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

public class EventFilters implements Serializable {
    private static final long serialVersionUID = 7242501391010513215L;

    public static final String EXTRAS_KEY = "extras.filters";

    public String type;
    public String text;
    public double maxDistance;

    private transient ParseGeoPoint currentLocation;

    public EventFilters() {
        this.reset();
    }

    public void reset() {
        this.type = "";
        this.text = "";
        this.maxDistance = 0;
        this.currentLocation = null;
    }

    public void setCurrentLocation(ParseGeoPoint geoPoint) {
        this.currentLocation = geoPoint;
    }

    public void setCurrentLocation(double latitude, double longitude) {
        this.currentLocation = new ParseGeoPoint(latitude, longitude);
    }

    public void applyFiltersToEventQuery(ParseQuery<Event> query) {
        if (this.type != null && !this.type.isEmpty()) {
            query.whereEqualTo("type", this.type);
        }
    }

    public void applyFiltersToLocationUpdateQuery(ParseQuery<LocationUpdate> query) {
        if (this.type != null && !this.type.isEmpty()) {
            query.whereEqualTo("type", this.type);
        }
        if (this.currentLocation != null && this.maxDistance > 0) {
            query.whereWithinMiles("location", this.currentLocation, this.maxDistance);
        }
    }

    @Override
    public String toString() {
        return ("{type: " + this.type + "}, " +
                "{text: " + this.text + "}, " +
                "{maxDistance: " + this.maxDistance + "}");
    }
}
