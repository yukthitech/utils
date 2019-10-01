package com.yukthitech.autox.test.lang.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.autox.test.Function;
import com.yukthitech.autox.test.log.LogLevel;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Loops through specified range of values and for each iteration executed underlying steps
 * 
 * @author akiran
 */
@Executable(name = "for", message = "Loops through specified range of values and for each iteration executed underlying steps")
public class ForLoopStep extends AbstractStep implements IStepContainer
{
	private static final long serialVersionUID = 1L;

	/**
	 * Group of steps/validations to be executed when condition evaluated to be
	 * true.
	 */
	@SkipParsing
	@Param(description = "Group of steps/validations to be executed in loop.")
	private Function steps;

	/**
	 * Inclusive start of range.
	 */
	@Param(description = "Inclusive start of range.", sourceType = SourceType.EXPRESSION)
	private Object start;
	
	/**
	 * Inclusive end of range.
	 */
	@Param(description = "Inclusive end of range.", sourceType = SourceType.EXPRESSION)
	private Object end;

	/**
	 * Loop variable that will be used to set loop iteration object on context. Default: loopVar.
	 */
	@Param(description = "Loop variable that will be used to set loop iteration object on context. Default: loopVar", required = false)
	private String loopVar = "loopVar";

	/**
	 * Sets the inclusive start of range.
	 *
	 * @param start the new inclusive start of range
	 */
	public void setStart(Object start)
	{
		this.start = start;
	}

	/**
	 * Sets the inclusive end of range.
	 *
	 * @param end the new inclusive end of range
	 */
	public void setEnd(Object end)
	{
		this.end = end;
	}
	
	/**
	 * Sets the loop variable that will be used to set loop iteration object on context. Default: loopVar.
	 *
	 * @param loopVar the new loop variable that will be used to set loop iteration object on context
	 */
	public void setLoopVar(String loopVar)
	{
		this.loopVar = loopVar;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStepContainer#addStep(com.yukthitech.autox.IStep)
	 */
	@Override
	public void addStep(IStep step)
	{
		if(steps == null)
		{
			steps = new Function();
		}
		
		steps.addStep(step);
	}

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger) throws Exception
	{
		int start = 0, end = 0;
		
		try
		{
			start = Integer.parseInt(this.start.toString());
		}catch(Exception ex)
		{
			exeLogger.log(LogLevel.ERROR, "Invalid/non-int-convertable start value specified: {}", this.start);
			throw new InvalidArgumentException("Invalid/non-int-convertable start value specified: {}", this.start);
		}
		
		try
		{
			end = Integer.parseInt(this.end.toString());
		}catch(Exception ex)
		{
			exeLogger.log(LogLevel.ERROR, "Invalid/non-int-convertable end value specified: {}", this.end);
			throw new InvalidArgumentException("Invalid/non-int-convertable end value specified: {}", this.end);
		}
		
		if(start > end)
		{
			exeLogger.log(LogLevel.ERROR, "Start value '{}' is greater than end value- {}", start, end);
			throw new InvalidArgumentException("Start value '{}' is greater than end value- {}", start, end);
		}

		for(int i = start; i <= end; i++)
		{
			context.setAttribute(loopVar, i);
			
			try
			{
				steps.execute(context, exeLogger, true);
			} catch(BreakException ex)
			{
				break;
			} catch(ContinueException ex)
			{
				continue;
			}
		}
		
		return true;
	}
}
