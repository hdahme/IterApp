package com.example.mapdemo.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFacebookUtils.Permissions.User;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Event")
public class Event extends ParseObject implements Serializable{
	private static final long serialVersionUID = 1L;

	public Event() {
        super();
    }

    public boolean isActive() {
        return getBoolean("active");
    }

    public void setActive(boolean active) {
        put("active", active);
    }

    public String getType() {
        return getString("type");
    }

    public EventType getTypeObject() {
        return EventType.fromValue(this.getType());
    }

    public void setType(String type) {
        put("type", type);
    }

    public void setType(EventType type) {
        put("type", type.getValue());
    }

    public String getId() {
    	return getString("objectId");
    }

    public ParseUser getOwner() {
        return getParseUser("owner");
    }

    public void setOwner(ParseUser user) {
        put("owner", user);
    }

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String description) {
        put("description", description);
    }
    
    public int getNumberOfParticipants() {
    	return getParticipants().size();
    }
    
    public ArrayList<String> getParticipants() {
    	List<Object> objs = getList("participants");
    	ArrayList<String> a = new ArrayList<String>();
    	for (Object o : objs){
    		a.add((String) o);
    	}
    	return a;
    }
    
    public void setParticipants(List<ParseUser> p) {
    	ArrayList<String> a = new ArrayList<String>();
    	for (ParseUser u : p) {
    		a.add(u.getObjectId());
    	}
    	put("participants", a);
    }
    
    public List<String> addParticipant(ParseUser u) {
    	addUnique("participants", u.getObjectId());
    	return getParticipants();
    }
    
    public List<String> removeParticipant(ParseUser u){
    	removeAll("participants", Arrays.asList(u.getObjectId()));
    	return getParticipants();
    }
}
