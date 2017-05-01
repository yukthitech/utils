/**
 * 
 */
package com.yukthitech.utils.rest;

import org.apache.http.HttpResponse;

/**
 * Represents result of REST api invocation
 * 
 * @author akiran
 */
public class RestResult<T>
{
	/**
	 * Result of rest API invocation, if any
	 */
	private T value;

	/**
	 * Http response status code
	 */
	private int statusCode;
	
	/**
	 * Actual http response recieved by Rest Client
	 */
	private HttpResponse httpResponse;

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

		builder.append("]");
		return builder.toString();
	}

}
