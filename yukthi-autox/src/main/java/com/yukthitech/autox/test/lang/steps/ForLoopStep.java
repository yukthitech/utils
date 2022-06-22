package com.yukthitech.autox.test.lang.steps;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ChildElement;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.autox.exec.AutomationExecutor;
import com.yukthitech.autox.exec.IExecutionStackEntry;
import com.yukthitech.autox.test.log.LogLevel;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Loops through specified range of values and for each iteration executed underlying steps
 * 
 * @author akiran
 */
@Executable(name = "for", group = Group.Lang, message = "Loops through specified range of values and for each iteration executed underlying steps")
public class ForLoopStep extends AbstractStep implements IStepContainer
{
	private static final long serialVersionUID = 1L;

	private static final String VAR_INDEX = "index";

	private static final String VAR_START = "start";

	private static final String VAR_END = "end";

	/**
	 * Group of steps/validations to be executed when condition evaluated to be
	 * true.
	 */
	@SkipParsing
	@Param(description = "Group of steps/validations to be executed in loop.")
	private List<IStep> steps = new ArrayList<IStep>();

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
	@Param(description = "Loop variable that will be used to set loop iteration object on context. Default: loopVar", required = false,
			attrName = true, defaultValue = "loopVar")
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
	@ChildElement(description = "Steps to be executed")
	@Override
	public void addStep(IStep step)
	{
		steps.add(step);
	}
	
	@Override
	public List<IStep> getSteps()
	{
		return steps;
	}
	
	private boolean populateRange(ExecutionLogger exeLogger, IExecutionStackEntry stackEntry)
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
			return false;
		}
		
		stackEntry.setVariable(VAR_START, start);
		stackEntry.setVariable(VAR_END, end);
		stackEntry.setVariable(VAR_INDEX, new AtomicInteger(start));
		return true;
	}
	
	@Override
	public void execute(AutomationContext context, ExecutionLogger exeLogger) throws Exception
	{
		AutomationExecutor executor = context.getAutomationExecutor();
		
		executor.newSteps("for-each-steps", this, steps)
			.onInit(entry -> 
			{
				return populateRange(exeLogger, entry);
			})
			.onPreexecute(entry -> 
			{
				AtomicInteger loopIndex = (AtomicInteger) entry.getVariable(VAR_INDEX);
				int idx = loopIndex.get();
				
				//execute the steps
				context.setAttribute(loopVar, idx);
			})
			.exceptionHandler((entry, ex) -> 
			{
				AtomicInteger loopIndex = (AtomicInteger) entry.getVariable(VAR_INDEX);
				Integer end = (Integer) entry.getVariable(VAR_END);
				
				if(ex instanceof BreakException)
				{
					entry.skipChildSteps();
					loopIndex.set(end + 1);
					return true;
				}
				
				if(ex instanceof ContinueException)
				{
					entry.resetChildIndex();
					return true;
				}
				
				return false;
			})
			.onSuccess(entry -> 
			{
				AtomicInteger loopIndex = (AtomicInteger) entry.getVariable(VAR_INDEX);
				loopIndex.incrementAndGet();
			})
			.isReexecutionNeeded(entry -> 
			{
				AtomicInteger loopIndex = (AtomicInteger) entry.getVariable(VAR_INDEX);
				Integer end = (Integer) entry.getVariable(VAR_END);
				
				int idx = loopIndex.incrementAndGet();
				
				return (idx <= end);
			})
			.execute();
		;
	}
}
