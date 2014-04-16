package com.example.mapdemo.models;

import java.util.Comparator;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("LocationUpdate")
public class LocationUpdate extends ParseObject implements Comparable<LocationUpdate>{
	
	public LocationUpdate() {
		super();
	}
	public double getLat() {
		return getDouble("lat");
	}
	public double getLng() {
		return getDouble("lng");
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
	public void setLat(double lat) {
		put("lat", lat);
	}
	public void setLng(double lng) {
		put("lng", lng);
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
}
