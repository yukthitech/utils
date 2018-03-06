package com.yukthitech.autox.test.common.steps;

import java.util.concurrent.TimeUnit;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.test.TestCaseFailedException;

/**
 * Sleeps for specified amount of time.
 * @author akiran
 */
@Executable(name = "sleep", message = "Sleeps for specified amount of time.")
public class SleepStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Time to sleep.
	 */
	@Param(description = "Time to sleep.")
	private Long time;
	
	/**
	 * Units of time specified. Default: millis.
	 */
	@Param(description = "Units of time specified. Default: millis", required = false)
	private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
	
	/**
	 * Sets the time to sleep.
	 *
	 * @param time the new time to sleep
	 */
	public void setTime(Long time)
	{
		this.time = time;
	}
	
	/**
	 * Sets the units of time specified. Default: millis.
	 *
	 * @param timeUnit the new units of time specified
	 */
	public void setTimeUnit(TimeUnit timeUnit)
	{
		this.timeUnit = timeUnit;
	}

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger) 
	{
		exeLogger.debug(this, "Sleeping for {} {}", time, timeUnit);
		
		try
		{
			Thread.sleep( timeUnit.toMillis(time) );
		} catch(InterruptedException ex)
		{
			exeLogger.error(this, "Sleep step is interrupted", ex);
			throw new TestCaseFailedException(this, "Sleep step is interrupted", ex);
		}
		
		return true;
	}
}
