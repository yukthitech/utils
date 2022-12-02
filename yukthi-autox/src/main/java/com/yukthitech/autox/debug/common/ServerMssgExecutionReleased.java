package com.yukthitech.autox.debug.common;

import java.io.Serializable;

/**
 * Used when execution is release from pause state of debug point.
 * @author akranthikiran
 */
public class ServerMssgExecutionReleased implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Unique id representing current step or execution.
	 */
	private String executionId;
	
	public ServerMssgExecutionReleased(String executionId)
	{
		this.executionId = executionId;
	}

	public String getExecutionId()
	{
		return executionId;
	}
}
