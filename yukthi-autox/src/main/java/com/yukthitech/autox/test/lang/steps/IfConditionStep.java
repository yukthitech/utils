package com.yukthitech.autox.test.lang.steps;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

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
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.autox.test.Function;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Evaluates specified condition and if evaluates to true execute 'then'
 * otherwise execute 'else'. For ease 'if' supports direct addition of steps which would be added to then block.
 * 
 * @author akiran
 */
@Executable(name = "if", group = Group.Lang, message = "Evaluates specified condition and if evaluates to true execute 'then' otherwise execute 'else'. "
		+ "For ease 'if' supports direct addition of steps which would be added to then block.")
public class IfConditionStep extends AbstractStep implements IStepContainer
{
	private static final long serialVersionUID = 1L;

	/**
	 * Freemarker condition to be evaluated.
	 */
	@Param(description = "Freemarker condition to be evaluated.", required = true, sourceType = SourceType.CONDITION)
	private String condition;

	/**
	 * Group of steps/validations to be executed when condition evaluated to be
	 * true.
	 */
	@SkipParsing
	@Param(description = "Group of steps/validations to be executed when condition evaluated to be true.", required = true)
	private List<IStep> then = new ArrayList<IStep>();

	/**
	 * Group of steps/validations to be executed when condition evaluated to be
	 * false.
	 */
	@SkipParsing
	@Param(name = "else", description = "Group of steps/validations to be executed when condition evaluated to be false.", required = false)
	private List<IStep> elseGroup = new ArrayList<IStep>();

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
	@ChildElement(description = "Used to group steps to be executed when this if condition is true.")
	public void setThen(Function then)
	{
		this.then.addAll(then.getSteps());
	}

	/**
	 * Sets the group of steps/validations to be executed when condition evaluated to be false.
	 *
	 * @param elseGroup the new group of steps/validations to be executed when condition evaluated to be false
	 */
	@ChildElement(description = "Used to group steps to be executed when this if condition is false.")
	public void setElse(Function elseGroup)
	{
		if(CollectionUtils.isNotEmpty(this.elseGroup))
		{
			throw new InvalidStateException("else group is already defined.");
		}
		
		this.elseGroup.addAll(elseGroup.getSteps());
	}
	
	@Override
	public void addStep(IStep step)
	{
		then.add(step);
	}

	@Override
	public List<IStep> getSteps()
	{
		return then;
	}

	@Override
	public void execute(AutomationContext context, ExecutionLogger exeLogger) throws Exception
	{
		boolean res = AutomationUtils.evaluateCondition(context, condition);
		
		exeLogger.debug("Condition evaluation resulted in '{}'. Condition: {}", res, condition);
		
		if(res)
		{
			context.getAutomationExecutor()
				.newSteps("if-then-steps", this, then)
				.execute();
		}
		else
		{
			if(CollectionUtils.isNotEmpty(elseGroup))
			{
				context.getAutomationExecutor()
					.newSteps("if-then-steps", this, elseGroup)
					.execute();
			}
		}
	}
}
