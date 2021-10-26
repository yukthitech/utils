package com.yukthitech.autox.test.rest.steps;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.config.RestPlugin;
import com.yukthitech.utils.rest.DeleteRestRequest;

/**
 * Used to invoke DELETE REST API.
 * @author akiran
 */
@Executable(name = "restInvokeDelete", group = Group.Rest_Api, requiredPluginTypes = RestPlugin.class, message = "Used to invoke DELETE api.")
public class InvokeDeleteStep extends AbstractRestWithBodyStep<DeleteRestRequest>
{
	private static final long serialVersionUID = 1L;
	
	protected DeleteRestRequest newRequest(String uri)
	{
		return new DeleteRestRequest(uri);
	}
}
