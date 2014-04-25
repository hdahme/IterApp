package com.example.mapdemo;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class FacebookUsersAdapter extends ArrayAdapter<FacebookUser> {
    public FacebookUsersAdapter(Context context, ArrayList<FacebookUser> aMovies) {
        super(context, 0, aMovies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        FacebookUser user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.facebook_user, null);
        }
        // Lookup views within item layout
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        ImageView ivPosterImage = (ImageView) convertView.findViewById(R.id.ivFacebookUser);
        // Populate the data into the template view using the data object
        tvUserName.setText(user.getTitle());

        Picasso.with(getContext()).load(user.getPosterUrl()).into(ivPosterImage);
        // Return the completed view to render on screen
        return convertView;
    }
    
}