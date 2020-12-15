package com.yukthitech.autox.test.rest.steps;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.config.RestPlugin;
import com.yukthitech.utils.rest.PostRestRequest;

/**
 * Used to invoke Multi part POST REST API.
 * @author akiran
 */
@Executable(name = "restInvokeMultipartPost", group = Group.Rest_Api, requiredPluginTypes = RestPlugin.class, message = "Used to invoke Multipart POST api.")
public class InvokePostWithAttachmentStep extends AbstractRestWithAttachmentsStep<PostRestRequest>
{
	private static final long serialVersionUID = 1L;
	
	@Override
	public PostRestRequest newRequest(String uri)
	{
		return new PostRestRequest(uri);
	}
}
