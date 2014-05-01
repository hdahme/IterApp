package com.iterapp.helpers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import android.util.Log;

import com.google.android.gms.maps.model.UrlTileProvider;

public class MapBoxTileProvider extends UrlTileProvider {
	
	private static final String FORMAT = "http://api.tiles.mapbox.com/v3/%s/%d/%d/%d.png";
	private String mMapIdentifier;

	public MapBoxTileProvider(String mapIdentifier) {
		super(256, 256);
		this.mMapIdentifier = mapIdentifier;
	}

	@Override
	public URL getTileUrl(int x, int y, int z) {
		try {
			URL u = new URL(String.format(Locale.US, FORMAT, this.mMapIdentifier, z, x, y));
            return u;
        }
        catch (MalformedURLException e) {
        	Log.d("fbId", e.getMessage());
            return null;
        }
		catch (Exception e) {
			Log.d("fbId", e.getMessage());
            return null;
		}
	}

}
