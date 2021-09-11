package com.yukthitech.autox.test.rest.steps;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.config.RestPlugin;
import com.yukthitech.utils.rest.GetRestRequest;

/**
 * Used to invoke Multi part GET REST API.
 * @author akiran
 */
@Executable(name = "restInvokeMultipartGet", group = Group.Rest_Api, requiredPluginTypes = RestPlugin.class, message = "Used to invoke Multipart GET api.")
public class InvokeGetWithAttachmentStep extends AbstractRestWithAttachmentsStep<GetRestRequest>
{
	private static final long serialVersionUID = 1L;
	
	@Override
	public GetRestRequest newRequest(String uri)
	{
		return new GetRestRequest(uri);
	}
}
