package com.yukthitech.autox.action;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.test.Function;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Represents action plan file.
 * @author akiran
 */
public class ActionPlanFile implements Validateable
{
	/**
	 * Action plan.
	 */
	private ActionPlan actionPlan;
	
	/**
	 * Context to be used.
	 */
	private AutomationContext context;
	
	/**
	 * Instantiates a new action plan file.
	 *
	 * @param context the context
	 */
	public ActionPlanFile(AutomationContext context)
	{
		this.context = context;
	}

	/**
	 * Sets the action plan.
	 *
	 * @param actionPlan the new action plan
	 */
	public void setActionPlan(ActionPlan actionPlan)
	{
		this.actionPlan = actionPlan;
	}
	
	/**
	 * Gets the action plan.
	 *
	 * @return the action plan
	 */
	public ActionPlan getActionPlan()
	{
		return actionPlan;
	}
	
	/**
	 * Adds the step group.
	 *
	 * @param function the step group
	 */
	public void addFunction(Function function)
	{
		context.addFunction(function);
	}

	@Override
	public void validate() throws ValidateException
	{
		if(actionPlan == null)
		{
			throw new ValidateException("No action plan is specified.");
		}
	}
}
