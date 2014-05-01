package com.iterapp;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iterapp.R;
import com.facebook.model.GraphObject;

public class FacebookUsersAdapter extends ArrayAdapter<GraphObject> {
    public FacebookUsersAdapter(Context context, ArrayList<GraphObject> aMovies) {
        super(context, 0, aMovies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        GraphObject user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.facebook_user, null);
        }
        // Lookup views within item layout
        // Populate the data into the template view using the data object
       
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        String facebookName = (String)user.getProperty("name");
        tvUserName.setText(facebookName);
      
        /*
        TextView tvUserLink = (TextView) convertView.findViewById(R.id.tvUserLink);
        String facebookLink = (String)user.getProperty("link");
        tvUserLink.setText(facebookLink);
        */
        
        ImageView ivUserImage = (ImageView) convertView.findViewById(R.id.ivFacebookUser);
        String imageURL = "http://graph.facebook.com/"+(String)user.getProperty("id")+"/picture";
        //Picasso.with(getContext()).load(imageURL).into(ivUserImage);
        // Return the completed view to render on screen
        
        new DownloadImageTask(ivUserImage)
        .execute(imageURL);
        
        return convertView;
    }
    
}