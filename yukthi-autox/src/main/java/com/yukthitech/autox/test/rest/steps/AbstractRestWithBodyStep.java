package com.yukthitech.autox.test.rest.steps;

import com.yukthitech.autox.ChildElement;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.config.RestPlugin;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.utils.rest.RestRequestWithBody;

/**
 * Used to invoke POST REST API.
 * @author akiran
 */
@Executable(name = "restInvokePost", group = Group.Rest_Api, requiredPluginTypes = RestPlugin.class, message = "Used to invoke POST api.")
public abstract class AbstractRestWithBodyStep<T extends RestRequestWithBody<T>> extends AbstractRestStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Body to be set. If non-string is specified, object will be converted to json and content-type header will be set as JSON.
	 */
	@Param(description = "Body to be set. If non-string is specified, object will be converted to json and content-type header will be set as JSON.", required = false, sourceType = SourceType.EXPRESSION)
	private Object body;
	
	/**
	 * Content char set.
	 */
	@Param(description = "Content char set to be used for body.", required = false)
	private String contentCharset;
	
	/**
	 * Sets the body to be set. If non-string is specified, object will be converted to json and content-type header will be set as JSON.
	 *
	 * @param body the new body to be set
	 */
	@ChildElement(description = "Body of the request")
	public void setBody(Object body)
	{
		this.body = body;
	}
	
	/**
	 * Sets the content char set.
	 *
	 * @param contentCharset
	 *            the new content char set
	 */
	public void setContentCharset(String contentCharset)
	{
		this.contentCharset = contentCharset;
	}
	
	protected abstract T newRequest(String uri);
	
	@Override
	public void execute(AutomationContext context, IExecutionLogger logger) throws Exception
	{
		T postRestRequest = newRequest(uri);
		
		if(body instanceof String)
		{
			postRestRequest.setBody((String) body);
		}
		else if(body != null)
		{
			postRestRequest.setJsonBody(body);
		}
		
		if(contentCharset != null)
		{
			postRestRequest.setContentCharset(contentCharset);
		}
		
		super.populate(context, postRestRequest, logger);
		super.invoke(context, postRestRequest, logger);
	}

}
