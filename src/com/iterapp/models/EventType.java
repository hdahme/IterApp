package com.iterapp.models;

import java.util.HashMap;

import com.iterapp.R;

public enum EventType {
    BIKE("bike", "Cycling", R.drawable.ic_bike),
    HIKE("hike", "Hiking", R.drawable.ic_hike),
    PUB_CRAWL("bar_crawl", "Pub Crawl", R.drawable.ic_beer);

    private static final HashMap<String, EventType> eventValueToTypeMap;
    private static final HashMap<String, EventType> eventDisplayValueToTypeMap;
    
    static
    {
        eventValueToTypeMap = new HashMap<String, EventType>();
        eventDisplayValueToTypeMap = new HashMap<String, EventType>();
        for (EventType eventType : EventType.values()) {
            EventType.eventValueToTypeMap.put(eventType.value, eventType);
            EventType.eventDisplayValueToTypeMap.put(eventType.displayValue, eventType);
        }
    }

    private String value;
    private String displayValue;
    private Integer drawableId;
    
    private EventType(String value, String displayValue, Integer drawableId) {
        this.value = value;
        this.displayValue = displayValue;
        this.drawableId = drawableId;
    }

    public static EventType fromValue(String value) {
        if (eventValueToTypeMap.containsKey(value)) {
            return eventValueToTypeMap.get(value);
        }
        return null;
    }

    public static EventType fromDisplayValue(String displayValue) {
        if (eventDisplayValueToTypeMap.containsKey(displayValue)) {
            return eventDisplayValueToTypeMap.get(displayValue);
        }
        return null;
    }

    public String getValue() {
        return this.value;
    }

    public String getDisplayValue() {
        return this.displayValue;
    }

    public Integer getDrawableId() {
        return this.drawableId;
    }

    @Override
    public String toString() {
        return this.displayValue;
    }
}
