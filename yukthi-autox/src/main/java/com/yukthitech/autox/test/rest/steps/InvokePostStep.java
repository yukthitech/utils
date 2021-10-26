package com.yukthitech.autox.test.rest.steps;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.config.RestPlugin;
import com.yukthitech.utils.rest.PostRestRequest;

/**
 * Used to invoke POST REST API.
 * @author akiran
 */
@Executable(name = "restInvokePost", group = Group.Rest_Api, requiredPluginTypes = RestPlugin.class, message = "Used to invoke POST api.")
public class InvokePostStep extends AbstractRestWithBodyStep<PostRestRequest>
{
	private static final long serialVersionUID = 1L;
	
	protected PostRestRequest newRequest(String uri)
	{
		return new PostRestRequest(uri);
	}
}
