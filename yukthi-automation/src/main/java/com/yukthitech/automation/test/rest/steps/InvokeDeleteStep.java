package com.yukthitech.automation.test.rest.steps;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.ExecutionLogger;
import com.yukthitech.automation.config.RestPlugin;
import com.yukthitech.utils.rest.DeleteRestRequest;

/**
 * Used to invoke DELETE REST API.
 * @author akiran
 */
@Executable(name = "invokeDelete", requiredPluginTypes = RestPlugin.class, message = "Used to invoke DELETE api.")
public class InvokeDeleteStep extends AbstractRestStep
{
	private static final long serialVersionUID = 1L;
	
	@Override
	public void execute(AutomationContext context, ExecutionLogger logger) throws Exception
	{
		DeleteRestRequest deleteRestRequest = new DeleteRestRequest(uri);
		
		super.populate(context, deleteRestRequest, logger);
		super.invoke(context, deleteRestRequest, logger);
	}

}
