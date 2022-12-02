package com.yukthitech.autox.debug.common;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.InvalidArgumentException;

/**
 * Used in interactive environments to execute steps.
 * @author akiran
 */
public class ClientMssgExecuteSteps extends ClientMessage
{
	private static final long serialVersionUID = 1L;
	
	private String executionId;

	private String stepsToExecute;

	public ClientMssgExecuteSteps(String executionId, String stepsToExecute)
	{
		super(UUID.randomUUID().toString());
		
		if(StringUtils.isEmpty(stepsToExecute))
		{
			throw new InvalidArgumentException("Steps to execute cannot be empty");
		}

		if(StringUtils.isEmpty(executionId))
		{
			throw new InvalidArgumentException("Execution-id cannot be empty");
		}
		
		this.executionId = executionId;
		this.stepsToExecute = stepsToExecute;
	}
	
	public String getExecutionId()
	{
		return executionId;
	}

	public String getStepsToExecute()
	{
		return stepsToExecute;
	}
}
