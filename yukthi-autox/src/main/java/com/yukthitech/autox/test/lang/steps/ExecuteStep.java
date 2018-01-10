package com.yukthitech.autox.test.lang.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.common.AutomationUtils;

/**
 * Used to execute specified expression.
 * @author akiran
 */
@Executable(name = "execute", message = "Execute specified expression.")
public class ExecuteStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Expression to execute.
	 */
	@Param(description = "Expression to execute.")
	private String expression;
	
	/**
	 * If true, encloses the specified expression in ${}. Default: true.
	 */
	@Param(description = "If true, encloses the specified expression in ${}. Default: true")
	private boolean enclose = true;
	
	/**
	 * Sets the expression to execute.
	 *
	 * @param expression the new expression to execute
	 */
	public void setExpression(String expression)
	{
		this.expression = expression;
	}
	
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger) 
	{
		String finalExpr = enclose ? "${" + expression + "}" : expression;
		
		exeLogger.debug(this, "Executing expression: {}", finalExpr);
		
		String output = AutomationUtils.replaceExpressions(context, finalExpr);
		exeLogger.debug(this, "Output of expression execution: {}", output);
		
		return true;
	}
}
