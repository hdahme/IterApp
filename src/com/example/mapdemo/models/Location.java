package com.example.mapdemo.models;

import java.io.Serializable;

public class Location implements Serializable {
    private static final long serialVersionUID = 9164529005280807568L;

    private double lat;
	private double lng;
	private int timestamp;
	private int userId;
	private int eventId;
	
	public Location(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}
	public double getLat() {
		return lat;
	}
	public double getLng() {
		return lng;
	}
	public int getTimestamp() {
		return timestamp;
	}
	public int getUserId() {
		return userId;
	}
	public int getEventId() {
		return eventId;
	}

}
