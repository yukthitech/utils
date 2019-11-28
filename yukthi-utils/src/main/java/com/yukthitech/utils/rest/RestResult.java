/**
 * 
 */
package com.yukthitech.utils.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;

/**
 * Represents result of REST api invocation
 * 
 * @author akiran
 */
public class RestResult<T> implements Serializable
{
	
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Result of rest API invocation, if any
	 */
	private T value;

	/**
	 * Http response status code
	 */
	private int statusCode;
	
	/**
	 * Response headers.
	 */
	private Map<String, List<String>> headers = new HashMap<String, List<String>>();
	
	/**
	 * Parse error, if any, during response parsing.
	 */
	private String parseError;
	
	/**
	 * Actual http response recieved by Rest Client
	 */
	private transient HttpResponse httpResponse;

	/**
	 * @param value
	 * @param statusCode
	 */
	public RestResult(T value, int statusCode, HttpResponse response)
	{
		this.value = value;
		this.statusCode = statusCode;
		this.httpResponse = response;
	}

	/**
	 * Gets value of value
	 * 
	 * @return the value
	 */
	public T getValue()
	{
		return value;
	}

	/**
	 * Gets value of statusCode
	 * 
	 * @return the statusCode
	 */
	public int getStatusCode()
	{
		return statusCode;
	}
	
	/**
	 * Gets the actual http response.
	 *
	 * @return the {@link #httpResponse httpResponse}
	 */
	public HttpResponse getHttpResponse()
	{
		return httpResponse;
	}
	
	/**
	 * Adds the specified response header.
	 * @param header header to add.
	 * @param value value to set
	 */
	public void addHeader(String header, String value)
	{
		List<String> values = this.headers.get(header);
		
		if(values == null)
		{
			values = new ArrayList<String>();
			headers.put(header, values);
		}
		
		values.add(value);
	}

	/**
	 * Gets the response headers.
	 *
	 * @return the response headers
	 */
	public Map<String, List<String>> getHeaders()
	{
		return headers;
	}
	
	/**
	 * Fetches the first value of specified header.
	 * @param name header to fetch 
	 * @return matching value
	 */
	public String getHeaderValue(String name)
	{
		List<String> values = headers.get(name);
		
		if(values == null)
		{
			return null;
		}
		
		return values.get(0);
	}
	
	/**
	 * Fetches all values of specified header.
	 * @param name name of header to fetch
	 * @return matching header values
	 */
	public List<String> getHeaderValues(String name)
	{
		return headers.get(name);
	}
	
	/**
	 * Gets the parse error, if any, during response parsing.
	 *
	 * @return the parse error, if any, during response parsing
	 */
	public String getParseError()
	{
		return parseError;
	}

	/**
	 * Sets the parse error, if any, during response parsing.
	 *
	 * @param parseError the new parse error, if any, during response parsing
	 */
	public void setParseError(String parseError)
	{
		this.parseError = parseError;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Status Code: ").append(statusCode);
		builder.append(",").append("Value: ").append(value);
		
		if(parseError != null)
		{
			builder.append(",").append("Parse Error: ").append(parseError);
		}

		builder.append("]");
		return builder.toString();
	}

}
