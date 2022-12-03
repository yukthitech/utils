/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
