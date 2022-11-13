package com.yukthitech.autox.test.lang.steps;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ChildElement;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.autox.exec.StepsExecutor;

/**
 * Loops through specified range of values and for each iteration executed underlying steps
 * 
 * @author akiran
 */
@Executable(name = "while", group = Group.Lang, message = "Loops till specified condition is evaluated to true executed underlying steps")
public class WhileLoopStep extends AbstractStep implements IStepContainer
{
	private static final long serialVersionUID = 1L;

	/**
	 * Group of steps/validations to be executed when condition evaluated to be
	 * true.
	 */
	@SkipParsing
	@Param(description = "Group of steps/validations to be executed in loop.")
	private List<IStep> steps = new ArrayList<IStep>();

	/**
	 * Freemarker condition to be evaluated.
	 */
	@Param(description = "Freemarker condition to be evaluated.", sourceType = SourceType.CONDITION)
	private String condition;

	/**
	 * Sets the freemarker condition to be evaluated.
	 *
	 * @param condition the new freemarker condition to be evaluated
	 */
	public void setCondition(String condition)
	{
		this.condition = condition;
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
	
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger) throws Exception
	{
		while(AutomationUtils.evaluateCondition(context, condition))
		{
			try
			{
				StepsExecutor.execute(exeLogger, steps, null);
			}catch(Exception ex)
			{
				if(ex instanceof BreakException)
				{
					break;
				}
				
				if(ex instanceof ContinueException)
				{
					continue;
				}
				
				throw ex;
			}
		}
	}
}
