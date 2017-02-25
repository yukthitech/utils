package com.yukthitech.ccg.xml;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic bean that can be used to accept dynamic properties.
 * 
 * @author akiran
 */
public class DynamicBean implements DynamicDataAcceptor, IDynamicAttributeAcceptor
{
	/**
	 * Map to hold dynamic properties and values.
	 */
	private Map<String, Object> properties = new HashMap<String, Object>();

	@Override
	public void set(String propName, String value)
	{
		properties.put(propName, value);
	}

	@Override
	public void add(String propName, Object obj)
	{
		properties.put(propName, obj);
	}

	@Override
	public void add(String propName, String id, Object obj)
	{}

	@Override
	public boolean isIDBased(String arg0)
	{
		return false;
	}

	/**
	 * Gets the map to hold dynamic properties and values.
	 *
	 * @return the map to hold dynamic properties and values
	 */
	public Map<String, Object> getProperties()
	{
		return properties;
	}

	public Object get(String name)
	{
		return properties.get(name);
	}
}
