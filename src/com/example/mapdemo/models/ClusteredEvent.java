package com.example.mapdemo.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;

public class ClusteredEvent implements ClusterItem {

	Marker mapItem;
	LatLng valueLoc;
	double longVal;
	double latVal;
	
	MarkerOptions options;
	
	public ClusteredEvent(){
		
	}
			
	
	@Override
	public LatLng getPosition() {
		// TODO Auto-generated method stub
		//return new LatLng(latVal, longVal);
		//return mapItem.getPosition();
		return valueLoc;
	}
	
	public void setMarker(Marker mark){
		mapItem = mark;
	}
	
	public void setMarker(LatLng value){
		valueLoc = value;
	}
	
	public void setLong(double longVal){
		this.longVal = longVal;
	}
	
	public void setLat(double latVal){
		this.latVal = latVal;
	}
	
	public void setMarkerOptions(MarkerOptions opt){
		this.options = opt;
	}
	
	public MarkerOptions getMarkerOptions(){
		return options;
	}
	
	
	
}
