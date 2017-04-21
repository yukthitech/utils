package com.yukthitech.autox.test.lang.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.common.AutomationUtils;

/**
 * Evaluates specified condition and if evaluates to true execute 'then'
 * otherwise execute 'else'.
 * 
 * @author akiran
 */
@Executable(name = "if", message = "Evaluates specified condition and if evaluates to true execute 'then' otherwise execute 'else'")
public class IfConditionStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Freemarker condition to be evaluated.
	 */
	@Param(description = "Freemarker condition to be evaluated.")
	private String condition;

	/**
	 * Group of steps/validations to be executed when condition evaluated to be
	 * true.
	 */
	@Param(description = "Group of steps/validations to be executed when condition evaluated to be true.")
	private StepGroup then;

	/**
	 * Group of steps/validations to be executed when condition evaluated to be
	 * false.
	 */
	@Param(name = "else", description = "Group of steps/validations to be executed when condition evaluated to be false.")
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
		this.elseGroup = elseGroup;
	}

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger) throws Exception
	{
		boolean res = AutomationUtils.evaluateCondition(context, condition);
		
		exeLogger.debug("Condition evaluation resulted in '{}'. Condition: {}", res, condition);
		
		if("true".equals(res))
		{
			then.execute(context, exeLogger);
		}
		else
		{
			elseGroup.execute(context, exeLogger);
		}
		
		return true;
	}
}
