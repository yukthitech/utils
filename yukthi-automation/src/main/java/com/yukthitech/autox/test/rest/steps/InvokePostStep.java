package com.yukthitech.autox.test.rest.steps;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.config.RestPlugin;
import com.yukthitech.utils.rest.PostRestRequest;

/**
 * Used to invoke POST REST API.
 * @author akiran
 */
@Executable(name = "invokePost", requiredPluginTypes = RestPlugin.class, message = "Used to invoke POST api.")
public class InvokePostStep extends AbstractRestStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Body to be set. If non-string is specified, object will be converted to json and content-type header will be set as JSON.
	 */
	@Param(description = "Body to be set. If non-string is specified, object will be converted to json and content-type header will be set as JSON.")
	private Object body;
	
	/**
	 * Sets the body to be set. If non-string is specified, object will be converted to json and content-type header will be set as JSON.
	 *
	 * @param body the new body to be set
	 */
	public void setBody(Object body)
	{
		this.body = body;
	}
	
	@Override
	public void execute(AutomationContext context, ExecutionLogger logger) throws Exception
	{
		PostRestRequest postRestRequest = new PostRestRequest(uri);
		
		if(body instanceof String)
		{
			postRestRequest.setBody((String) body);
		}
		else
		{
			postRestRequest.setJsonBody(body);
		}
		
		super.populate(context, postRestRequest, logger);
		super.invoke(context, postRestRequest, logger);
	}

}
