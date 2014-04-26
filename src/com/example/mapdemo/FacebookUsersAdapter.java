package com.example.mapdemo;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.model.GraphUser;
import com.squareup.picasso.Picasso;

public class FacebookUsersAdapter extends ArrayAdapter<GraphUser> {
    public FacebookUsersAdapter(Context context, ArrayList<GraphUser> aMovies) {
        super(context, 0, aMovies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        GraphUser user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.facebook_user, null);
        }
        // Lookup views within item layout
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        TextView tvUserLink = (TextView) convertView.findViewById(R.id.tvUserLink);
        
        ImageView ivUserImage = (ImageView) convertView.findViewById(R.id.ivFacebookUser);
        
        // Populate the data into the template view using the data object
        tvUserName.setText(user.getName());

        tvUserLink.setText(user.getLink());
        
        Picasso.with(getContext()).load("http://graph.facebook.com/"+user.getId()+"/picture?type=small").into(ivUserImage);
        // Return the completed view to render on screen
        return convertView;
    }
    
}