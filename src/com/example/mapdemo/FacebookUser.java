package com.example.mapdemo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FacebookUser {
    private String userName;
    private String hobbies;
    private String posterUrl;


    public String getTitle() {
        return userName;
    }


    public String getSynopsis() {
        return hobbies;
    }

    public String getPosterUrl() {
        return posterUrl;
    }
    
    public static FacebookUser fromJson(JSONObject jsonObject) {
        FacebookUser b = new FacebookUser();
        try {
            // Deserialize json into object fields
            b.userName = jsonObject.getString("name");
            b.hobbies = jsonObject.getString("hobbies");
            b.posterUrl = jsonObject.getJSONObject("profile_picture").getString("thumbnail");

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return b;
    }
    
    
    public static ArrayList<FacebookUser> fromJson(JSONArray jsonArray) {
        ArrayList<FacebookUser> businesses = new ArrayList<FacebookUser>(jsonArray.length());
        // Process each result in json array, decode and convert to business
        // object
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject businessJson = null;
            try {
                businessJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            FacebookUser business = FacebookUser.fromJson(businessJson);
            if (business != null) {
                businesses.add(business);
            }
        }

        return businesses;
    }

    
    
}
