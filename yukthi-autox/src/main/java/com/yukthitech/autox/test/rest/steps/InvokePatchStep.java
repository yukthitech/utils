package com.yukthitech.autox.test.rest.steps;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.config.RestPlugin;
import com.yukthitech.utils.rest.PatchRestRequest;

/**
 * Used to invoke PATCH REST API.
 * @author akiran
 */
@Executable(name = "restInvokePatch", group = Group.Rest_Api, requiredPluginTypes = RestPlugin.class, message = "Used to invoke PATCH api.")
public class InvokePatchStep extends AbstractRestWithBodyStep<PatchRestRequest>
{
	private static final long serialVersionUID = 1L;

	protected PatchRestRequest newRequest(String uri)
	{
		return new PatchRestRequest(uri);
	}
}
