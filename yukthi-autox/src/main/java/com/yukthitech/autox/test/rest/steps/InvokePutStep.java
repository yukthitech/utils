package com.yukthitech.autox.test.rest.steps;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.config.RestPlugin;
import com.yukthitech.utils.rest.PutRestRequest;

/**
 * Used to invoke PUT REST API.
 * @author akiran
 */
@Executable(name = "restInvokePut", group = Group.Rest_Api, requiredPluginTypes = RestPlugin.class, message = "Used to invoke PUT api.")
public class InvokePutStep extends AbstractRestWithBodyStep<PutRestRequest>
{
	private static final long serialVersionUID = 1L;

	protected PutRestRequest newRequest(String uri)
	{
		return new PutRestRequest(uri);
	}
}
