package com.yukthitech.autox.context;

import java.util.AbstractMap;
import java.util.Set;

public class ExecutionContextAttrMap extends AbstractMap<String, Object>
{
	private ExecutionContext parentContext;
	
	public ExecutionContextAttrMap(ExecutionContext parentContext)
	{
		this.parentContext = parentContext;
	}

	@Override
	public Set<Entry<String, Object>> entrySet()
	{
		return null;
	}

	@Override
	public synchronized Object get(Object key)
	{
		if(!(key instanceof String))
		{
			return null;
		}
		
		return parentContext.getAttribute((String) key);
	}
	
	@Override
	public synchronized Object put(String key, Object value)
	{
		parentContext.setAttribute(key, value);
		return null;
	}
	
	@Override
	public String toString()
	{
		return "ExectuionContextAttrMap";
	}
}
