package com.yukthitech.autox.test.lang.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.ccg.xml.util.ValidateException;

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
	private StepGroup steps;

	/**
	 * Inclusive start of range.
	 */
	@Param(description = "Inclusive start of range.")
	private int start;
	
	/**
	 * Inclusive end of range.
	 */
	@Param(description = "Inclusive end of range.")
	private int end;

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
	public void setStart(int start)
	{
		this.start = start;
	}

	/**
	 * Sets the inclusive end of range.
	 *
	 * @param end the new inclusive end of range
	 */
	public void setEnd(int end)
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
			steps = new StepGroup();
		}
		
		steps.addStep(step);
	}

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger) throws Exception
	{
		for(int i = start; i <= end; i++)
		{
			context.setAttribute(loopVar, i);
			
			try
			{
				steps.execute(context, exeLogger);
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
	
	@Override
	public void validate() throws ValidateException
	{
		super.validate();
		
		if(start > end)
		{
			throw new ValidateException(String.format("Start value is greater than end value. [Start: %s, End: %s]", start, end));
		}
	}
}
