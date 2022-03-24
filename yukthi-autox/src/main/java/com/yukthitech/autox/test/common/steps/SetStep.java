package com.yukthitech.autox.test.common.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.filter.ExpressionFactory;

/**
 * Sets the specified context attribute with specified value.
 * 
 * @author akiran
 */
@Executable(name = "set", group = Group.Common, message = "Sets the specified value using specified expression")
public class SetStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Expression to be used to set the value.
	 */
	@Param(description = "Expression to be used to set the value.", required = true, sourceType = SourceType.EXPRESSION_PATH, attrName = true)
	private String expression;

	/**
	 * Value of the attribute to set.
	 */
	@Param(description = "Value of the attribute to set. Default: empty string", required = false, sourceType = SourceType.EXPRESSION)
	private Object value = "";

	/**
	 * Sets the expression to be used to set the value.
	 *
	 * @param expression the new expression to be used to set the value
	 */
	public void setExpression(String expression)
	{
		this.expression = expression;
	}

	/**
	 * Sets the value of the attribute to set.
	 *
	 * @param value the new value of the attribute to set
	 */
	public void setValue(String value)
	{
		this.value = value;
	}
	
	@Override
	public void execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		ExpressionFactory.getExpressionFactory().setExpressionValue(context, expression, value);
	}
}
