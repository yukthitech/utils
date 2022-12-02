package com.yukthitech.autox.debug.common;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.InvalidArgumentException;

/**
 * Base class for client messages.
 * @author akranthikiran
 */
public class ClientMessage implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String requestId;
	
	protected ClientMessage(String requestId)
	{
		if(StringUtils.isEmpty(requestId))
		{
			throw new InvalidArgumentException("Request id cannot be null or empty");
		}
		
		this.requestId = requestId;
	}
	
	public String getRequestId()
	{
		return requestId;
	}
}
