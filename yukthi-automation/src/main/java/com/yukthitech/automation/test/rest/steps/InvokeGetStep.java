package com.yukthitech.automation.test.rest.steps;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.ExecutionLogger;
import com.yukthitech.automation.config.RestPlugin;
import com.yukthitech.utils.rest.GetRestRequest;

/**
 * Used to invoke GET REST API.
 * @author akiran
 */
@Executable(name = "invokeGet", requiredPluginTypes = RestPlugin.class, message = "Used to invoke GET api.")
public class InvokeGetStep extends AbstractRestStep
{
	private static final long serialVersionUID = 1L;
	
	@Override
	public void execute(AutomationContext context, ExecutionLogger logger) throws Exception
	{
		GetRestRequest getRestRequest = new GetRestRequest(uri);
		
		super.populate(context, getRestRequest, logger);
		super.invoke(context, getRestRequest, logger);
	}

}
