package com.yukthitech.autox.test.common.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;

/**
 * Stops the timer and keeps the elapsed time on context.
 * @author akiran
 */
@Executable(name = "stopTimer", group = Group.Common, message = "Stops the timer and keeps the elapsed time on context.")
public class StopTimerStep extends AbstractStep 
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
	public boolean execute(AutomationContext context, ExecutionLogger logger) throws Exception
	{
		logger.debug("Stopped timer with name: {}", name);
		
		Long startTime = (Long) context.getAttribute(name + ".startTime");
		
		if(startTime == null)
		{
			throw new IllegalStateException("No timer started with specified name: " + name);
		}

		long currentTime = System.currentTimeMillis();
		
		long diffInMillis = currentTime - startTime;
		long diffInSecs = diffInMillis / 1000;
		long diffInMins = diffInSecs / 60;
		long diffInHours = diffInMins / 60;
		
		diffInSecs = (diffInMins > 0) ? (diffInSecs % 60) : diffInMins;
		diffInMins = (diffInHours > 0) ? (diffInMins % 60) : diffInMins;
		
		StringBuilder timeTakenStr = new StringBuilder();
		
		if(diffInHours > 0)
		{
			timeTakenStr.append(diffInHours).append("Hr ");
		}
		
		if(diffInMins > 0)
		{
			timeTakenStr.append(diffInMins).append("Min ");
		}
		
		timeTakenStr.append(diffInSecs).append("Sec");
		
		context.setAttribute(name, timeTakenStr.toString());
		return true;
	}
}
