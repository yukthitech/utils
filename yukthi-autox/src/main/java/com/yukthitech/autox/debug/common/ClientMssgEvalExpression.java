package com.yukthitech.autox.debug.common;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.InvalidArgumentException;

/**
 * Used to evaluate expression as part of current debug thread.
 * @author akiran
 */
public class ClientMssgEvalExpression extends ClientMessage
{
	private static final long serialVersionUID = 1L;
	
	private String livePointId;

	private String expression;

	public ClientMssgEvalExpression(String livePointId, String expression)
	{
		super(UUID.randomUUID().toString());
		
		if(StringUtils.isEmpty(expression))
		{
			throw new InvalidArgumentException("Expresion cannot be empty");
		}

		if(StringUtils.isEmpty(livePointId))
		{
			throw new InvalidArgumentException("Live point-id cannot be empty");
		}

		this.livePointId = livePointId;
		this.expression = expression;
	}
	
	public String getLivePointId()
	{
		return livePointId;
	}

	public String getExpression()
	{
		return expression;
	}
}
