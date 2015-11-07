/**
 * 
 */
package com.yukthi.utils.rest;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.methods.HttpRequestBase;

/**
 * 
 * @author akiran
 */
public abstract class RestRequest<T extends RestRequest<T>>
{
	private static final Pattern PATH_VAR_PATTERN = Pattern.compile("\\{(\\w+)\\}");
	protected String uri;

	protected Map<String, String> pathVariables = new HashMap<String, String>();
	protected Map<String, String> params = new HashMap<String, String>();

	/**
	 * Map to hold headers. Identity hash map is used in order to allow multiple values
	 * with same names.
	 */
	protected Map<String, String> headers = new IdentityHashMap<String, String>();

	/**
	 * Content type of the request
	 */
	private String contentType;
	
	/**
	 * Indicates if this request is secured, secured request information will not be printed to log.
	 */
	private boolean secured = false;

	/**
	 * @param uri
	 */
	public RestRequest(String uri)
	{
		if(!uri.startsWith("/"))
		{
			uri = "/" + uri;
		}

		this.uri = uri;
	}
	
	/**
	 * @return the {@link #secured secured}
	 */
	public boolean isSecured()
	{
		return secured;
	}

	/**
	 * @param secured the {@link #secured secured} to set
	 */
	public void setSecured(boolean secured)
	{
		this.secured = secured;
	}

	@SuppressWarnings("unchecked")
	public T addPathVariable(String name, String value)
	{
		pathVariables.put(name, value);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T addParam(String name, String value)
	{
		params.put(name, value);
		return (T) this;
	}

	/**
	 * Gets value of params
	 * 
	 * @return the params
	 */
	public Map<String, String> getParams()
	{
		return params;
	}

	@SuppressWarnings("unchecked")
	public T addHeader(String name, String value)
	{
		headers.put(new String(name), value);
		return (T) this;
	}

	/**
	 * @return the {@link #headers headers}
	 */
	public Map<String, String> getHeaders()
	{
		return headers;
	}

	/**
	 * Gets value of uri
	 * 
	 * @return the uri
	 */
	public String getUri()
	{
		return uri;
	}

	/**
	 * @return the {@link #contentType contentType}
	 */
	public String getContentType()
	{
		return contentType;
	}

	/**
	 * @param contentType
	 *            the {@link #contentType contentType} to set
	 */
	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}

	public String getResolvedUri()
	{
		StringBuffer buffer = new StringBuffer();

		Matcher matcher = PATH_VAR_PATTERN.matcher(uri);
		String value = null;
		String name = null;

		while(matcher.find())
		{
			name = matcher.group(1);
			value = pathVariables.get(name);

			if(value == null)
			{
				throw new NullPointerException("No path-variable found for variable - " + name);
			}

			matcher.appendReplacement(buffer, value);
		}

		matcher.appendTail(buffer);
		return buffer.toString();
	}
	
	/**
	 * Populates configured headers on the specified request
	 * @param request
	 */
	protected void populateHeaders(HttpRequestBase request)
	{
		for(String name : headers.keySet())
		{
			request.addHeader(name, headers.get(name));
		}
	}

	public abstract HttpRequestBase toHttpRequestBase(String baseUrl) throws Exception;
}
