package com.yukthitech.automation.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.rest.RestClient;

public class RestPlugin implements IPlugin<Object>, Validateable
{
	/**
	 * Base url for REST api invocation.
	 */
	private String baseUrl;
	
	/**
	 * Default headers to be passed with every method invocation.
	 */
	private Map<String, String> defaultHeaders = new HashMap<>();
	
	/**
	 * Mapping from base url to client.
	 */
	private Map<String, RestClient> urlToClient = new HashMap<>();
	
	@Override
	public Class<Object> getArgumentBeanType()
	{
		return null;
	}

	@Override
	public void initialize(AutomationContext context, Object args)
	{
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
	public Map<String, String> getDefaultHeaders()
	{
		return defaultHeaders;
	}
	
	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(baseUrl))
		{
			throw new ValidateException("Base url can not be null.");
		}
	}

	/**
	 * Gets rest client for specified base url. If base url is not, default base url will be used.
	 * @param baseUrl Base url
	 * @return Client with specified base url.
	 */
	public synchronized RestClient getRestClient(String baseUrl)
	{
		baseUrl = StringUtils.isBlank(baseUrl) ? this.baseUrl : baseUrl;
		
		RestClient client = urlToClient.get(baseUrl);
		
		if(client == null)
		{
			client = new RestClient(baseUrl);
			urlToClient.put(baseUrl, client);
		}
		
		return client;
	}
}
