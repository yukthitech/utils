package com.yukthitech.automation.test.rest.steps;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.ExecutionLogger;
import com.yukthitech.automation.Param;
import com.yukthitech.automation.config.RestPlugin;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.rest.PostRestRequest;

/**
 * Used to invoke Multi part POST REST API.
 * @author akiran
 */
@Executable(name = "invokeMultipartPost", requiredPluginTypes = RestPlugin.class, message = "Used to invoke Multipart POST api.")
public class InvokePostWithAttachmentStep extends AbstractRestStep implements Validateable
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Parts to be set on the request. If non-string is specified, object will be converted to json and content-type of part will be set as JSON.
	 */
	@Param(description = "Parts to be set on the request. If non-string is specified, object will be converted to json and content-type of part will be set as JSON.", required = false)
	private Map<String, Object> parts = new HashMap<>();
	
	@Param(description = "List of files to be attachment with this request.", required = false)
	private Map<String, String> attachments = new HashMap<>();

	/**
	 * Parts to be set on the request. If non-string is specified, object will be converted to json and content-type of part will be set as JSON.
	 * @param name Name of the part to set.
	 * @param part part to be set.
	 */
	public void addPart(String name, Object part)
	{
		parts.put(name, part);
	}
	
	/**
	 * Adds the specified file as attachment.
	 * @param name Name of the field.
	 * @param file File path to be attached.
	 */
	public void addAttachment(String name, String file)
	{
		attachments.put(name, file);
	}
	
	@Override
	public void execute(AutomationContext context, ExecutionLogger exeLogger) throws Exception
	{
		PostRestRequest postRestRequest = new PostRestRequest(uri);
		postRestRequest.setMultipartRequest(true);
		
		//add parts of the request
		exeLogger.debug("Adding parts with names: {}", parts.keySet());
		
		for(Map.Entry<String, Object> partEntry : parts.entrySet())
		{
			if(partEntry.getValue() instanceof String)
			{
				postRestRequest.addTextPart(partEntry.getKey(), (String) partEntry.getValue());
			}
			else
			{
				postRestRequest.addJsonPart(partEntry.getKey(), partEntry.getValue());
			}
		}
		
		File file = null;
		
		//add attachments
		exeLogger.debug("Adding attachments with names: {}", attachments.keySet());
		
		for(Map.Entry<String, String> attachEntry : attachments.entrySet())
		{
			file = new File(attachEntry.getValue());
			
			if(!file.exists())
			{
				exeLogger.error("Attachment file '{}' specified for attachment '{}' does not exist", file.getPath(), attachEntry.getKey());
				throw new InvalidStateException("Invalid/non-existing file '{}' specified for attachment - {}", attachEntry.getValue(), attachEntry.getKey());
			}
			
			postRestRequest.addAttachment(attachEntry.getKey(), file, null);
		}
		
		super.populate(context, postRestRequest, exeLogger);
		super.invoke(context, postRestRequest, exeLogger);
	}

}
