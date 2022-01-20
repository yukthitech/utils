package com.yukthitech.autox;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * A map-wrapper over context which is generally used in JEL.
 * 
 * @author akranthikiran
 */
public class ContextMap extends AbstractMap<String, Object>
{
	private Map<String, Object> overrideMap = new HashMap<String, Object>();
	
	private AutomationContext context;
	
	public ContextMap(AutomationContext context)
	{
		this.context = context;
	}

	@Override
	public Set<Entry<String, Object>> entrySet()
	{
		throw new UnsupportedOperationException("This method is not supported by context-map");
	}

	@Override
	public Object get(Object key)
	{
		Object val = overrideMap.get(key);
		
		if(val != null)
		{
			return val;
		}
		
		try
		{
			return PropertyUtils.getProperty(context, key.toString());
		}catch (Exception e) 
		{
			return null;
		}
	}

	@Override
	public Object putIfAbsent(String key, Object value)
	{
		return overrideMap.put(key, value);
	}
}
