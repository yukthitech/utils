package com.yukthitech.autox.debug.common;

import java.io.Serializable;

/**
 * Used in interactive environments to execute steps.
 * @author akiran
 */
public class ExecuteStepsClientMssg implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String stepsToExecute;

	public ExecuteStepsClientMssg()
	{}
	
	public ExecuteStepsClientMssg(String stepsToExecute)
	{
		this.stepsToExecute = stepsToExecute;
	}

	public String getStepsToExecute()
	{
		return stepsToExecute;
	}

	public void setStepsToExecute(String stepsToExecute)
	{
		this.stepsToExecute = stepsToExecute;
	}
}
