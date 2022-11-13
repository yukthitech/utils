package com.yukthitech.autox.test.common.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IExecutionLogger;
import com.yukthitech.autox.Param;

/**
 * Starts time tracking with specified name. Stopping timer would keep elaspsed time on context which can used for logging.
 * @author akiran
 */
@Executable(name = "startTimer", group = Group.Common, message = "Starts time tracking with specified name. Stopping timer would keep elaspsed time on context which can used for logging.")
public class StartTimerStep extends AbstractStep 
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Name of the timer.
	 */
	@Param(description = "Name of the timer.")
	private String name;
	
	/**
	 * Sets the name of the timer.
	 *
	 * @param name the new name of the timer
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	@Override
	public void execute(AutomationContext context, IExecutionLogger logger) throws Exception
	{
		logger.debug("Started timer with name: {}", name);
		context.setAttribute(name + ".startTime", System.currentTimeMillis());
	}
}
