package com.yukthitech.automation.test;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.ccg.xml.DynamicDataAcceptor;
import com.yukthitech.ccg.xml.IDynamicAttributeAcceptor;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Map bean which provides convenient method to add property.
 * @author akiran
 */
public class MapData extends HashMap<String, Object> implements DynamicDataAcceptor, IDynamicAttributeAcceptor, Validateable
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Name of this map data.
	 */
	private String name;
	
	/**
	 * Sets the name of this map data.
	 *
	 * @param name the new name of this map data
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Gets the name of this map data.
	 *
	 * @return the name of this map data
	 */
	public String getName()
	{
		return name;
	}
	
	@Override
	public void add(String propName, Object obj)
	{
		super.put(propName, obj);
	}

	@Override
	public void add(String propName, String id, Object obj)
	{
	}

	@Override
	public boolean isIdBased(String propName)
	{
		return false;
	}

	@Override
	public void set(String attrName, String value)
	{
		super.put(attrName, value);
	}

	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(name))
		{
			throw new ValidateException("No/invalid name specified.");
		}
	}
}
