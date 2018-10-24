package com.yukthitech.autox.monitor.ienv;

import java.io.Serializable;

/**
 * Used in interactive environments to execute steps.
 * @author akiran
 */
public class InteractiveExecuteSteps implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String stepsToExecute;

	public InteractiveExecuteSteps()
	{}
	
	public InteractiveExecuteSteps(String stepsToExecute)
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
