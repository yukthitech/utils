package com.yukthitech.autox;

import com.yukthi.utils.fmarker.annotaion.FreeMarkerMethod;

/**
 * Freemarker methods specific to autox.
 * @author akiran
 */
public class AutoxFreeMarkerMethods
{
	/**
	 * Fetches value from story with specified key.
	 * @param key key to be fetched
	 * @return matching value from store
	 */
	@FreeMarkerMethod(value = "storeValue")
	public static Object storeValue(String key)
	{
		return AutomationContext.getInstance().getPersistenceStorage().get(key);
	}
}
