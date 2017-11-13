package com.yukthitech.ccg.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
		add(propName, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void add(String propName, Object obj)
	{
		if(!properties.containsKey(propName))
		{
			properties.put(propName, obj);
			return;
		}
		
		Object oldVal = properties.get(propName);
		List<Object> valLst = null;
		
		if(oldVal instanceof List)
		{
			valLst = (List<Object>) oldVal;
		}
		else
		{
			valLst = new ArrayList<Object>();
			valLst.add(oldVal);
			
			properties.put(propName, valLst);
		}
		
		valLst.add(obj);
	}

	@Override
	public void add(String propName, String id, Object obj)
	{}

	@Override
	public boolean isIdBased(String arg0)
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
	
	/**
	 * Converts specified object into simple map or list or simple value.
	 * @param obj object to be converted.
	 * @return converted value.
	 */
	@SuppressWarnings("unchecked")
	private Object toSimpleObject(Object obj)
	{
		if(obj instanceof DynamicBean)
		{
			return ((DynamicBean) obj).toSimpleMap();
		}

		if(obj instanceof List)
		{
			List<Object> newLst = new ArrayList<Object>();
			List<Object> oldLst = (List<Object>) obj;
			
			for(Object lstVal : oldLst)
			{
				if(lstVal instanceof DynamicBean)
				{
					lstVal = ((DynamicBean)lstVal).toSimpleMap();
				}
				
				newLst.add(lstVal);
			}
			
			return newLst;
		}
		
		if(obj instanceof Map)
		{
			Map<Object, Object> finalMap = new HashMap<Object, Object>();
			Map<Object, Object> oldMap = (Map<Object, Object>) obj;
			
			for(Object key : oldMap.keySet())
			{
				finalMap.put(key, toSimpleObject(oldMap.get(key)));
			}
			
			return finalMap;
		}
		
		return obj;
	}
	
	/**
	 * Converts dynamic bean into simple map.
	 * @return converted simple map
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> toSimpleMap()
	{
		Map<String, Object> finalMap = (Map<String, Object>) toSimpleObject(properties);
		return finalMap;
	}
}
