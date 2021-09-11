package com.yukthitech.autox.test.rest.steps;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.config.RestPlugin;
import com.yukthitech.utils.rest.DeleteRestRequest;

/**
 * Used to invoke Multi part DELETE REST API.
 * @author akiran
 */
@Executable(name = "restInvokeMultipartDelete", group = Group.Rest_Api, requiredPluginTypes = RestPlugin.class, message = "Used to invoke Multipart DELETE api.")
public class InvokeDeleteWithAttachmentStep extends AbstractRestWithAttachmentsStep<DeleteRestRequest>
{
	private static final long serialVersionUID = 1L;
	
	@Override
	public DeleteRestRequest newRequest(String uri)
	{
		return new DeleteRestRequest(uri);
	}
}
