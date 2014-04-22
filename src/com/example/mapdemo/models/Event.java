package com.example.mapdemo.models;

import java.io.Serializable;

import com.parse.ParseClassName;
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
    	return getInt("numberOfParticipants");
    }
    
    public void setNumberOfParticipants(int n) {
    	put("numberOfParticipants", n);
    }
}
