package com.example.mapdemo;

import java.io.Serializable;

import com.example.mapdemo.models.Event;
import com.parse.ParseQuery;

public class EventFilters implements Serializable {
    private static final long serialVersionUID = 7242501391010513215L;

    public static final String EXTRAS_KEY = "extras.filters";

    public String type;
    public String text;
    public double maxDistance;

    public EventFilters() {
        this.reset();
    }

    public void reset() {
        this.type = "";
        this.text = "";
        this.maxDistance = 0;
    }

    public void applyFiltersToQuery(ParseQuery<Event> query) {
        if (this.type != null && !this.type.isEmpty()) {
            query.whereEqualTo("type", this.type);
        }
    }

    @Override
    public String toString() {
        return ("{type: " + this.type + "}, " +
                "{text: " + this.text + "}, " +
                "{maxDistance: " + this.maxDistance + "}");
    }
}
