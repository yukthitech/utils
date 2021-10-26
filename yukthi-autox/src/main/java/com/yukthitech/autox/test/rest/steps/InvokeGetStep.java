package com.yukthitech.autox.test.rest.steps;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.config.RestPlugin;
import com.yukthitech.utils.rest.GetRestRequest;

/**
 * Used to invoke GET REST API.
 * @author akiran
 */
@Executable(name = "restInvokeGet", group = Group.Rest_Api, requiredPluginTypes = RestPlugin.class, message = "Used to invoke GET api.")
public class InvokeGetStep extends AbstractRestWithBodyStep<GetRestRequest>
{
	private static final long serialVersionUID = 1L;
	
	protected GetRestRequest newRequest(String uri)
	{
		return new GetRestRequest(uri);
	}
}
