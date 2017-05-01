package com.yukthitech.autox.test.rest.steps;

import java.io.Serializable;

/**
 * Represents part of the http request.
 * @author akiran
 */
public class HttpPart implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the part.
	 */
	private String name;
	
	/**
	 * Value for the part.
	 */
	private Object value;
	
	/**
	 * Content type of the part. By default is is json part.
	 */
	private String contentType = IRestConstants.JSON_CONTENT_TYPE;

	/**
	 * Gets the name of the part.
	 *
	 * @return the name of the part
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the part.
	 *
	 * @param name the new name of the part
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the value for the part.
	 *
	 * @return the value for the part
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * Sets the value for the part.
	 *
	 * @param value the new value for the part
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}

	/**
	 * Gets the content type of the part. By default is is json part.
	 *
	 * @return the content type of the part
	 */
	public String getContentType()
	{
		return contentType;
	}

	/**
	 * Sets the content type of the part. By default is is json part.
	 *
	 * @param contentType the new content type of the part
	 */
	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}
}
