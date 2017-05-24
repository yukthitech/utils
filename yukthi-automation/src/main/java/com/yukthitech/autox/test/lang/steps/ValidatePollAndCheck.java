package com.yukthitech.autox.test.lang.steps;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Evaluates specified condition and if evaluates to true execute 'then'
 * otherwise execute 'else'. For ease 'if' supports direct addition of steps which would be added to then block.
 * 
 * @author akiran
 */
@Executable(name = "pollAndCheck", message = "Used to execute polling steps till check condition is met with specified interval gap. Validation will fail if required condition is not met or exceeds timeout.")
public class ValidatePollAndCheck extends AbstractValidation
{
	private static final long serialVersionUID = 1L;

	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("dd/MM HH:mm:ss");
	
	/**
	 * Freemarker condition to be evaluated.
	 */
	@Param(description = "Check Freemarker condition to be evaluated.", required = true)
	private String checkCondition;

	/**
	 * Group of steps/validations to be executed when condition evaluated to be
	 * true.
	 */
	@Param(description = "Group of steps/validations to be executed as part of polling.", required = true)
	@SkipParsing
	private StepGroup poll;

	/**
	 * Polling interval duration in millis.
	 */
	@Param(description = "Polling interval duration in millis.", required = true)
	private Long pollingInterval;
	
	/**
	 * Timout in millis till which check condition will be tried. After this time, this validation will fail.
	 */
	@Param(description = "Timout in millis till which check condition will be tried. After this time, this validation will fail.", required = true)
	private Long timeOut;

	/**
	 * Sets the freemarker condition to be evaluated.
	 *
	 * @param checkCondition the new freemarker condition to be evaluated
	 */
	public void setCheckCondition(String checkCondition)
	{
		this.checkCondition = checkCondition;
	}

	/**
	 * Sets the group of steps/validations to be executed when condition evaluated to be true.
	 *
	 * @param poll the new group of steps/validations to be executed when condition evaluated to be true
	 */
	public void setPoll(StepGroup poll)
	{
		this.poll = poll;
	}
	
	/**
	 * Sets the polling interval duration in millis.
	 *
	 * @param pollingInterval the new polling interval duration in millis
	 */
	public void setPollingInterval(Long pollingInterval)
	{
		this.pollingInterval = pollingInterval;
	}

	/**
	 * Sets the timout in millis till which check condition will be tried. After this time, this validation will fail.
	 *
	 * @param timeOut the new timout in millis till which check condition will be tried
	 */
	public void setTimeOut(Long timeOut)
	{
		this.timeOut = timeOut;
	}

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger) throws Exception
	{
		Date startTime = new Date();
		long diff = 0;
		boolean res = false;
		
		exeLogger.debug("Starting time: {}. Check Condition: {}", TIME_FORMAT.format(startTime), checkCondition);
		
		while(true)
		{
			poll.execute(context, exeLogger);
			
			res = AutomationUtils.evaluateCondition(context, checkCondition);
			
			if(res)
			{
				exeLogger.debug("Check condition was successful. Finishing polling step");
				break;
			}
			
			diff = System.currentTimeMillis() - startTime.getTime();
			
			if(diff > timeOut)
			{
				exeLogger.error("Check condition '{}' is not met till timeout of {} millis. Error Time: {}", checkCondition, timeOut, TIME_FORMAT.format(new Date()));
				return false;
			}
			
			exeLogger.trace("Check condition was not met. Process will wait for {} millis before re-executing polling steps", pollingInterval);
			
			try
			{
				Thread.sleep(pollingInterval);
			}catch(Exception ex)
			{
				throw new InvalidStateException("Thread was interruped while waiting as part of polling step", ex);
			}
		}
		
		return true;
	}
}
