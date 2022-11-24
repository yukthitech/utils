package com.yukthitech.autox.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.utils.rest.RestClient;

public class RestPluginSession implements IPluginSession
{
	private RestPlugin parentPlugin;
	
	/**
	 * Mapping from base url to client.
	 */
	private Map<String, RestClient> urlToClient = new HashMap<>();

	public RestPluginSession(RestPlugin parentPlugin)
	{
		this.parentPlugin = parentPlugin;
	}
	
	@Override
	public RestPlugin getParentPlugin()
	{
		return parentPlugin;
	}
	
	/**
	 * Gets the default headers to be passed with every method invocation.
	 *
	 * @return the default headers to be passed with every method invocation
	 */
	public Map<String, String> getDefaultHeaders()
	{
		return Collections.unmodifiableMap(parentPlugin.getDefaultHeaders());
	}

	/**
	 * Gets rest client for specified base url. If base url is not, default base url will be used.
	 * @param baseUrl Base url
	 * @param proxy to be used.
	 * @return Client with specified base url.
	 */
	public synchronized RestClient getRestClient(String baseUrl, String proxy)
	{
		baseUrl = StringUtils.isBlank(baseUrl) ? parentPlugin.getBaseUrl() : baseUrl;

		//determine the cache key to be used
		String cacheKey = baseUrl;
		
		if(StringUtils.isNotBlank(proxy))
		{
			cacheKey = cacheKey + "@" + proxy;
		}
		
		RestClient client = urlToClient.get(cacheKey);
		
		if(client == null)
		{
			client = new RestClient(baseUrl, proxy);
			urlToClient.put(cacheKey, client);
		}
		
		return client;
	}
	
	@Override
	public void release()
	{
		parentPlugin.releaseSession(this);
	}

	@Override
	public void close()
	{
		for(RestClient restClient : this.urlToClient.values())
		{
			restClient.close();
		}
	}
}
