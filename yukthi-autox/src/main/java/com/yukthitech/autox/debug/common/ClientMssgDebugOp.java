package com.yukthitech.autox.debug.common;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.InvalidArgumentException;

/**
 * Used in interactive environments to execute steps.
 * @author akiran
 */
public class ClientMssgDebugOp extends ClientMessage
{
	private static final long serialVersionUID = 1L;
	
	private String executionId;

	private DebugOp debugOp;

	public ClientMssgDebugOp(String executionId, DebugOp debugOp)
	{
		super(UUID.randomUUID().toString());
		
		if(StringUtils.isBlank(executionId))
		{
			throw new InvalidArgumentException("Invalid execution id specified");
		}
		
		if(debugOp == null)
		{
			throw new InvalidArgumentException("Debug op cannot be null");
		}
		
		this.executionId = executionId;
		this.debugOp = debugOp;
	}
	
	public String getExecutionId()
	{
		return executionId;
	}

	public DebugOp getDebugOp()
	{
		return debugOp;
	}
}
