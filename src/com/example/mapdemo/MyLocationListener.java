package com.example.mapdemo;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

public class MyLocationListener implements LocationListener

{
	Context c;
	
	public MyLocationListener(Context context){
		c = context;
	}
	
@Override
	public void onLocationChanged(Location loc)
	{	
		loc.getLatitude();	
		loc.getLongitude();
		
		String Text = "My current location is: " +		
		"Latitud = " + loc.getLatitude() +		
		"Longitud = " + loc.getLongitude();		
		
		Toast.makeText(c, Text, Toast.LENGTH_SHORT).show();
	
	}


	@Override
	
	public void onProviderDisabled(String provider)
	
	{	
		Toast.makeText(c, "Gps Disabled",	Toast.LENGTH_SHORT ).show();
	
	}


	@Override
	
	public void onProviderEnabled(String provider)	
	{
		Toast.makeText(c, "Gps Enabled",	Toast.LENGTH_SHORT).show();
	
	}


	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
}

