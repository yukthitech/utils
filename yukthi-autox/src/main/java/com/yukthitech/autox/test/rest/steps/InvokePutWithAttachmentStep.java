package com.yukthitech.autox.test.rest.steps;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.config.RestPlugin;
import com.yukthitech.utils.rest.PutRestRequest;

/**
 * Used to invoke Multi part POST REST API.
 * @author akiran
 */
@Executable(name = "restInvokeMultipartPut", group = Group.Rest_Api, requiredPluginTypes = RestPlugin.class, message = "Used to invoke Multipart PUT api.")
public class InvokePutWithAttachmentStep extends AbstractRestWithAttachmentsStep<PutRestRequest>
{
	private static final long serialVersionUID = 1L;
	
	@Override
	public PutRestRequest newRequest(String uri)
	{
		return new PutRestRequest(uri);
	}
}
