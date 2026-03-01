package com.yukthitech.transform.event;

import com.yukthitech.transform.template.Location;

public class TransformEvent
{
    private Location location;

    private TransformEventType eventType;

    private String keyName;

    private Object result;

    public TransformEvent(Location location, TransformEventType eventType, Object result)
    {
        this.location = location;
        this.eventType = eventType;
        this.result = result;
    }

    public TransformEvent(Location location, TransformEventType eventType, String keyName, Object result)
    {
        this.location = location;
        this.eventType = eventType;
        this.keyName = keyName;
        this.result = result;
    }

    public Location getLocation()
    {
        return location;
    }

    public TransformEventType getEventType()
    {
        return eventType;
    }

    public String getKeyName()
    {
        return keyName;
    }

    public Object getResult()
    {
        return result;
    }
    
    @Override
    public String toString()
    {
    	return "Event [Type: %s, Key: %s, Result: %s, Location: %s]".formatted(eventType, keyName, result, location);
    }
}
