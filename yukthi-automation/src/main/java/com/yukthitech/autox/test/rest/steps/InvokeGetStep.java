package com.yukthitech.autox.test.rest.steps;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.config.RestPlugin;
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
