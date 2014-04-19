package com.example.mapdemo.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("LocationUpdate")
public class LocationUpdate extends ParseObject implements Comparable<LocationUpdate>,ClusterItem{
	
	private LatLng mPosition;
	
	public LocationUpdate() {
		super();
	}
	public ParseGeoPoint getLocation() {
	    return getParseGeoPoint("location");
	}
	public double getLat() {
	    ParseGeoPoint geoPoint = this.getLocation();
	    double value = 0;
	    if (geoPoint != null) {
	        value = geoPoint.getLatitude();
	    }
	    return value;
	}
	public double getLng() {
	    ParseGeoPoint geoPoint = this.getLocation();
        double value = 0;
        if (geoPoint != null) {
            value = geoPoint.getLongitude();
        }
        return value;
	}
	public long getTimestamp() {
		return getLong("timestamp");
	}
	public ParseUser getUser() {
		return getParseUser("user");
	}
	public ParseObject getEvent() {
		return getParseObject("event");
	}
	public String getType() {
		return getString("type");
	}
	public void setType(String type) {
		put("type", type);
	}
	public void setEvent(ParseObject event) {
		put("event", event);
	}
	public void setLocation(ParseGeoPoint geoPoint) {
	    put("location", geoPoint);
	}
	public void setLocation(double lat, double lng) {
	    ParseGeoPoint geoPoint = new ParseGeoPoint(lat, lng);
        put("location", geoPoint);
    }
	public void setLat(double lat) {
	    ParseGeoPoint geoPoint = this.getLocation();
	    if (geoPoint == null) {
	        geoPoint = new ParseGeoPoint();
	    }
	    geoPoint.setLatitude(lat);
	    put("location", geoPoint);
	}
	public void setLng(double lng) {
	    ParseGeoPoint geoPoint = this.getLocation();
	    if (geoPoint == null) {
            geoPoint = new ParseGeoPoint();
        }
        geoPoint.setLongitude(lng);
        put("location", geoPoint);
	}
	public void setUser(ParseUser user) {
		put("user", user);
	}
	public void setTimestamp(long timestamp) {
		put("timestamp", timestamp);
	}

	public int compareTo(LocationUpdate other) {
		return this.getEvent().getObjectId().compareTo(other.getEvent().getObjectId());
	}
	
	public String toString() {
		return this.getEvent().getObjectId() + ",  " + this.getLat() + ",  " + 
			this.getLng() + ",  " + this.getTimestamp();
	}
	@Override
	public LatLng getPosition() {
		return getMyPosLatLng();
	}
	private LatLng getMyPosLatLng() {
		// TODO Auto-generated method stub
		return new LatLng(getLat(), getLng()); 
	}
}
