package com.yukthitech.autox.test.rest.steps;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.config.RestPlugin;
import com.yukthitech.utils.rest.PatchRestRequest;

/**
 * Used to invoke Multi part PATCH REST API.
 * @author akiran
 */
@Executable(name = "restInvokeMultipartPatch", group = Group.Rest_Api, requiredPluginTypes = RestPlugin.class, message = "Used to invoke Multipart PATCH api.")
public class InvokePatchWithAttachmentStep extends AbstractRestWithAttachmentsStep<PatchRestRequest>
{
	private static final long serialVersionUID = 1L;
	
	@Override
	public PatchRestRequest newRequest(String uri)
	{
		return new PatchRestRequest(uri);
	}
}
