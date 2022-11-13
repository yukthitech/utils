package com.yukthitech.autox.test.lang.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.common.AutomationUtils;

/**
 * Used to execute specified expression.
 * @author akiran
 */
@Executable(name = "execute", group = Group.Lang, message = "Execute specified freemarker expression.")
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
	
	public void setEnclose(boolean enclose)
	{
		this.enclose = enclose;
	}
	
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger) 
	{
		String finalExpr = enclose ? "${" + expression + "}" : expression;
		
		exeLogger.debug("Executing expression: {}", finalExpr);
		
		String output = AutomationUtils.replaceExpressionsInString("finalExpr", context, finalExpr);
		exeLogger.debug("Output of expression execution: {}", output);
	}
}
