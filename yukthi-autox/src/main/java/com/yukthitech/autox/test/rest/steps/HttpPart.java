package com.yukthitech.autox.test.rest.steps;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Represents part of the http request.
 * @author akiran
 */
public class HttpPart implements Serializable, Validateable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the part.
	 */
	@Param(name = "name", description = "Name of the part", required = true)
	private String name;
	
	/**
	 * Value for the part.
	 */
	@Param(name = "value", description = "Value for the part. Can be object or resource source.", required = true, sourceType = SourceType.EXPRESSION)
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

	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(name))
		{
			throw new ValidateException("Name can not be null or blank");
		}
		
		if(value == null)
		{
			throw new ValidateException("Value can not be null.");
		}
	}
}
