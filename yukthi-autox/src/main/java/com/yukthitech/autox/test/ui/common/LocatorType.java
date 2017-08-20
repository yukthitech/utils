package com.yukthitech.autox.test.ui.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines types of locators that can be used for automation.
 * @author akiran
 */
public enum LocatorType
{
	/**
	 * Element id based locator.
	 */
	ID("id"),
	
	/**
	 * Element's CSS class based locator.
	 */
	CLASS("class"),
	
	/**
	 * Element's tag name based locator.
	 */
	TAG("tag"),
	
	/**
	 * Element's name based locator.
	 */
	NAME("name"),
	
	/**
	 * CSS expression based locator.
	 */
	CSS("css"),
	
	/**
	 * Xpath based locator.
	 */
	XPATH("xpath"),
	
	/**
	 * JS expression based locator.
	 */
	JS("js")
	
	;
	
	/**
	 * Mapping from key to locator type.
	 */
	private static Map<String, LocatorType> keyToType = null;

	/**
	 * Prefix used in locator string to use particular locator type.
	 */
	private String key;
	
	/**
	 * Instantiates a new locator type.
	 *
	 * @param key the key
	 */
	private LocatorType(String key)
	{
		this.key = key;
	}
	
	/**
	 * Gets the prefix used in locator string to use particular locator type.
	 *
	 * @return the prefix used in locator string to use particular locator type
	 */
	public String getKey()
	{
		return key;
	}
	
	/**
	 * Fetches the locator type based on specified key.
	 * @param key Key for which locator type to be fetched
	 * @return Matching locator type
	 */
	public static LocatorType getLocatorType(String key)
	{
		if(keyToType != null)
		{
			return keyToType.get(key);
		}
		
		keyToType = new HashMap<String, LocatorType>();
		
		for(LocatorType type : LocatorType.values())
		{
			keyToType.put(type.key, type);
		}
		
		return keyToType.get(key);
	}
}
