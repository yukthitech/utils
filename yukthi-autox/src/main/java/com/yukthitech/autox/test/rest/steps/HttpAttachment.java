package com.yukthitech.autox.test.rest.steps;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.Param;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Represents the attachment that can be sent along with request.
 * @author akiran
 */
public class HttpAttachment implements Validateable, Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the attachment.
	 */
	@Param(name = "name", description = "Name of the attachment", required = true)
	private String name;
	
	/**
	 * File of the attachment.
	 */
	@Param(name = "file", description = "Resource to be added as attachment", required = true)
	private String file;
	
	/**
	 * Flag indicating if the attachment should be parsed as free marker template.
	 */
	private boolean parseAsTemplate = false;
	
	/**
	 * Instantiates a new http attachment.
	 */
	public HttpAttachment()
	{}

	/**
	 * Instantiates a new http attachment.
	 *
	 * @param name the name
	 * @param file the file
	 * @param parseAsTemplate the parse as template
	 */
	public HttpAttachment(String name, String file, boolean parseAsTemplate)
	{
		this.name = name;
		this.file = file;
		this.parseAsTemplate = parseAsTemplate;
	}

	/**
	 * Gets the name of the attachment.
	 *
	 * @return the name of the attachment
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the attachment.
	 *
	 * @param name the new name of the attachment
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the file of the attachment.
	 *
	 * @return the file of the attachment
	 */
	public String getFile()
	{
		return file;
	}

	/**
	 * Sets the file of the attachment.
	 *
	 * @param file the new file of the attachment
	 */
	public void setFile(String file)
	{
		this.file = file;
	}

	/**
	 * Gets the flag indicating if the attachment should be parsed as free marker template.
	 *
	 * @return the flag indicating if the attachment should be parsed as free marker template
	 */
	public boolean isParseAsTemplate()
	{
		return parseAsTemplate;
	}

	/**
	 * Sets the flag indicating if the attachment should be parsed as free marker template.
	 *
	 * @param parseAsTemplate the new flag indicating if the attachment should be parsed as free marker template
	 */
	public void setParseAsTemplate(boolean parseAsTemplate)
	{
		this.parseAsTemplate = parseAsTemplate;
	}

	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(name))
		{
			throw new ValidateException("Name can not be null or empty");
		}
		
		if(StringUtils.isBlank(file))
		{
			throw new ValidateException("File can not be null or empty");
		}
	}
}
