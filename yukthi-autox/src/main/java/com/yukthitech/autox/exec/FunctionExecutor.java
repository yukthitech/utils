package com.yukthitech.autox.exec;

import java.util.Arrays;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.test.Function;
import com.yukthitech.autox.test.FunctionRef;

/**
 * Function executor to execute specified function.
 * @author akiran
 */
public class FunctionExecutor extends Executor
{
	private static final String RETURN_ATTR = "functionExecutor.returnAttr";
	
	public FunctionExecutor(Function function)
	{
		super(function, null);
		super.childSteps = Arrays.asList(new FunctionRef(function.getName(), RETURN_ATTR));
	}

	public Object executeFunction()
	{
		super.execute(null, null);
		return AutomationContext.getInstance().getAttribute(RETURN_ATTR);
	}
}
