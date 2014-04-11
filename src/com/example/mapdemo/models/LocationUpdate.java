package com.example.mapdemo.models;

import java.io.Serializable;

public class LocationUpdate implements Serializable {
    private static final long serialVersionUID = 9164529005280807568L;

    private double lat;
	private double lng;
	private int timestamp;
	private int userId;
	private String eventId;
	private String type;
	
	public LocationUpdate(double lat, double lng, String type) {
		this.lat = lat;
		this.lng = lng;
		this.type = type;
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
	public String getEventId() {
		return eventId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	

}
