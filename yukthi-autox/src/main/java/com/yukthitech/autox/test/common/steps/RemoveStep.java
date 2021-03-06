package com.yukthitech.autox.test.common.steps;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.filter.ExpressionFactory;
import com.yukthitech.ccg.xml.util.ValidateException;

/**
 * Removes the specified context attribute.
 * 
 * @author akiran
 */
@Executable(name = "remove", group = Group.Common, message = "Removes the specified context attribute or values matching with specified path.")
public class RemoveStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of attribute to set.
	 */
	@Param(description = "Name of the attribute to remove.", required = false)
	private String name;
	
	@Param(description = "Expression to be used to remove the values. Currently supported expressions: xpath, attr, store", required = false)
	private String expression;

	/**
	 * Sets the name of attribute to set.
	 *
	 * @param name the new name of attribute to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setExpression(String expression)
	{
		this.expression = expression;
	}

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		if(StringUtils.isNotBlank(name))
		{
			exeLogger.debug("Removing context attribute '{}'", name);
			context.removeAttribute(name);
		}
		
		if(StringUtils.isNotBlank(expression))
		{
			exeLogger.debug("Removing data using expression: {}", expression);
			ExpressionFactory.getExpressionFactory().removeByExpression(context, expression);	
		}
		
		return true;
	}
	
	@Override
	public void validate() throws ValidateException
	{
		super.validate();
		
		if(StringUtils.isBlank(name) && StringUtils.isBlank(expression))
		{
			throw new ValidateException("Both name and expression are not specified.");
		}
	}
}
