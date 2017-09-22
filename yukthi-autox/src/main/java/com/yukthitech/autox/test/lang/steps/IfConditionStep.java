package com.yukthitech.autox.test.lang.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.test.StepGroup;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Evaluates specified condition and if evaluates to true execute 'then'
 * otherwise execute 'else'. For ease 'if' supports direct addition of steps which would be added to then block.
 * 
 * @author akiran
 */
@Executable(name = "if", message = "Evaluates specified condition and if evaluates to true execute 'then' otherwise execute 'else'. "
		+ "For ease 'if' supports direct addition of steps which would be added to then block.")
public class IfConditionStep extends AbstractStep implements IStepContainer
{
	private static final long serialVersionUID = 1L;

	/**
	 * Freemarker condition to be evaluated.
	 */
	@Param(description = "Freemarker condition to be evaluated.", required = true)
	private String condition;

	/**
	 * Group of steps/validations to be executed when condition evaluated to be
	 * true.
	 */
	@Param(description = "Group of steps/validations to be executed when condition evaluated to be true.", required = true)
	private StepGroup then;

	/**
	 * Group of steps/validations to be executed when condition evaluated to be
	 * false.
	 */
	@Param(name = "else", description = "Group of steps/validations to be executed when condition evaluated to be false.", required = false)
	private StepGroup elseGroup;

	/**
	 * Sets the freemarker condition to be evaluated.
	 *
	 * @param condition the new freemarker condition to be evaluated
	 */
	public void setCondition(String condition)
	{
		this.condition = condition;
	}

	/**
	 * Sets the group of steps/validations to be executed when condition evaluated to be true.
	 *
	 * @param then the new group of steps/validations to be executed when condition evaluated to be true
	 */
	public void setThen(StepGroup then)
	{
		this.then = then;
	}

	/**
	 * Sets the group of steps/validations to be executed when condition evaluated to be false.
	 *
	 * @param elseGroup the new group of steps/validations to be executed when condition evaluated to be false
	 */
	public void setElse(StepGroup elseGroup)
	{
		if(this.elseGroup != null)
		{
			throw new InvalidStateException("else group is already defined.");
		}
		
		this.elseGroup = elseGroup;
	}
	
	@Override
	public void addStep(IStep step)
	{
		if(then == null)
		{
			then = new StepGroup();
		}
		
		then.addStep(step);
	}

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger) throws Exception
	{
		boolean res = AutomationUtils.evaluateCondition(context, condition);
		
		exeLogger.debug("Condition evaluation resulted in '{}'. Condition: {}", res, condition);
		
		if(res)
		{
			then.execute(context, exeLogger);
		}
		else
		{
			if(elseGroup != null)
			{
				elseGroup.execute(context, exeLogger);
			}
		}
		
		return true;
	}
}
