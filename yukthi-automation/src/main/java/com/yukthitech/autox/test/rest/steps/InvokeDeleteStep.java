package com.yukthitech.autox.test.rest.steps;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.config.RestPlugin;
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
	public boolean execute(AutomationContext context, ExecutionLogger logger) throws Exception
	{
		DeleteRestRequest deleteRestRequest = new DeleteRestRequest(uri);
		
		super.populate(context, deleteRestRequest, logger);
		super.invoke(context, deleteRestRequest, logger);
		return true;
	}

}
