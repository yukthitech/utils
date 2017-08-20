package com.yukthitech.autox.config;

import java.util.HashMap;
import java.util.Map;

import com.yukthitech.ccg.xml.XMLBeanParser;


/**
 * Internal configuration required by automation.
 * @author akiran
 */
public class AutomationConfiguration
{
	/**
	 * Singleton instance.
	 */
	private static AutomationConfiguration instance;
	
	/**
	 * Java script snippet that can be used in automation utils. 
	 */
	private Map<String, String> scripts = new HashMap<>();
	
	/**
	 * Means of getting singleton instance.
	 * @return Single ton instance of this class.
	 */
	public static AutomationConfiguration getInstance()
	{
		if(instance != null)
		{
			return instance;
		}
		
		instance = new AutomationConfiguration();
		XMLBeanParser.parse(AutomationConfiguration.class.getResourceAsStream("/automation-config.xml"), instance);
		return instance;
	}
	
	/**
	 * Adds specified script with specified name.
	 * @param name Name of the script snippet.
	 * @param script Script snippet.
	 */
	public void addScript(String name, String script)
	{
		scripts.put(name, script);
	}
	
	/**
	 * Fetches script with specified name.
	 * @param name Name of the script to fetch.
	 * @return matching script.
	 */
	public String getScript(String name)
	{
		return scripts.get(name);
	}
}
