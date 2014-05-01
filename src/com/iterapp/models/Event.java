package com.iterapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        String id = user.getString("fbId");
        put("facebookOwner", id);
    }
    
    
    public String getFacebookOwner() {
        return getString("facebookOwner");
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
    	
    	if(objs == null || objs.size() == 0)
    		return a;
    
    	for (Object o : objs){
    		a.add((String) o);
    	}
    	return a;
    }
    
    
    public ArrayList<String> getFacebookParticipants() {
    	List<Object> objs = getList("participants_facebookID");
    	ArrayList<String> a = new ArrayList<String>();
    	
    	if(objs == null || objs.size() == 0){
    		return a;
    	}
    
    	for (Object o : objs){
    		a.add((String) o);
    	}
    	return a;
    }
    
    public void setParticipants(List<ParseUser> p) {
    	ArrayList<String> a_id = new ArrayList<String>();
    	ArrayList<String> facebook_id = new ArrayList<String>();
    	for (ParseUser u : p) {
    		a_id.add(u.getObjectId());
    		facebook_id.add(u.getString("fbId"));
    	}
    	put("participants", a_id);
    	put("participants_facebookID", facebook_id);
    }
    
    public List<String> addParticipant(ParseUser u) {
    	addUnique("participants", u.getObjectId());
    	addUnique("participants_facebookID", u.getString("fbId"));
    	return getParticipants();
    }
    
    public List<String> removeParticipant(ParseUser u){
    	removeAll("participants", Arrays.asList(u.getObjectId()));
    	removeAll("participants_facebookID", Arrays.asList(u.getString("fbId")));
    	return getParticipants();
    }
}
