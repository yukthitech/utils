package com.yukthitech.autox.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.InvalidArgumentException;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Plugin for REST based steps and validations.
 * @author akiran
 */
@Executable(name = "RestPlugin", group = Group.NONE, message = "Plugin for REST based steps and validations.")
public class RestPlugin implements IPlugin<Object, RestPluginSession>, Validateable
{
	/**
	 * Base url for REST api invocation.
	 */
	@Param(description = "Default base url to be used for REST steps and validations.", required = true)
	private String baseUrl;
	
	/**
	 * Default headers to be passed with every method invocation.
	 */
	@Param(description = "Default HTTP headers that will be added before sending the request. The headers defined at step/validation level will override this header."
			+ "<b>The values can contain free-marker expressions.</b>", required = false)
	private Map<String, String> defaultHeaders = new HashMap<>();
	
	@Param(description = "Maximum number of sessions that can be opened simultaneously. Defaults to 10.")
	private int maxSessions = 10;
	
	private PluginCache<RestPluginSession> sessionCache;

	@Override
	public Class<Object> getArgumentBeanType()
	{
		return null;
	}

	@Override
	public void initialize(Object args)
	{
		sessionCache = new PluginCache<>(() -> new RestPluginSession(this), maxSessions);
	}
	
	public void setMaxSessions(int maxSessions)
	{
		if(maxSessions < 1)
		{
			throw new InvalidArgumentException("Invalid number of max sessions specified: " + maxSessions);
		}
		
		this.maxSessions = maxSessions;
	}

	/**
	 * Gets the base url for REST api invocation.
	 *
	 * @return the base url for REST api invocation
	 */
	public String getBaseUrl()
	{
		return baseUrl;
	}

	/**
	 * Sets the base url for REST api invocation.
	 *
	 * @param baseUrl the new base url for REST api invocation
	 */
	public void setBaseUrl(String baseUrl)
	{
		this.baseUrl = baseUrl;
	}

	/**
	 * Adds default header with specified name and value. 
	 * @param name Name of header to add
	 * @param value value of header to add.
	 */
	public void addDefaultHeader(String name, String value)
	{
		defaultHeaders.put(name, value);
	}
	
	/**
	 * Gets the default headers to be passed with every method invocation.
	 *
	 * @return the default headers to be passed with every method invocation
	 */
	Map<String, String> getDefaultHeaders()
	{
		return defaultHeaders;
	}
	
	@Override
	public RestPluginSession newSession()
	{
		return sessionCache.getSession();
	}
	
	void releaseSession(RestPluginSession session)
	{
		sessionCache.release(session);
	}
	
	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(baseUrl))
		{
			throw new ValidateException("Base url can not be null.");
		}
	}
}
