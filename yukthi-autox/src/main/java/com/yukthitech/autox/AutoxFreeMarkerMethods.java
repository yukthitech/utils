package com.yukthitech.autox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthi.utils.fmarker.annotaion.FreeMarkerMethod;

/**
 * Freemarker methods specific to autox.
 * @author akiran
 */
public class AutoxFreeMarkerMethods
{
	/**
	 * Used to convert objects into json.
	 */
	private static ObjectMapper objectMapper = new ObjectMapper();
	
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
	
	/**
	 * Converts input object into json string.
	 * @param obj object to convert
	 * @return converted json
	 * @throws Exception
	 */
	@FreeMarkerMethod(value = "toJson")
	public static String toJson(Object obj) throws Exception
	{
		return objectMapper.writeValueAsString(obj);
	}
}
