package com.yukthitech.autox.test.rest.steps;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.config.RestPlugin;
import com.yukthitech.autox.resource.IResource;
import com.yukthitech.autox.resource.ResourceFactory;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
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
	private List<HttpPart> parts = new ArrayList<>();
	
	@Param(description = "List of files to be attachment with this request.", required = false)
	private Map<String, String> attachments = new HashMap<>();

	/**
	 * Parts to be set on the request. If non-string is specified, object will be converted to json and content-type of part will be set as JSON.
	 * @param part part to be set.
	 */
	public void addPart(HttpPart part)
	{
		parts.add(part);
	}
	
	/**
	 * Adds the specified file as attachment.
	 * @param name Name of the field.
	 * @param file File path to be attached.
	 */
	public void addAttachment(String name, String file)
	{
		file = file.trim();
		
		if(file.length() == 0)
		{
			throw new InvalidArgumentException("Invalid file value specified: {}", file);
		}
		
		attachments.put(name, file.trim());
	}
	
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger) throws Exception
	{
		PostRestRequest postRestRequest = new PostRestRequest(uri);
		postRestRequest.setMultipartRequest(true);
		
		for(HttpPart partEntry : parts)
		{
			if(partEntry.getValue() instanceof String)
			{
				postRestRequest.addTextPart(partEntry.getName(), (String) partEntry.getValue(), partEntry.getContentType());
			}
			else
			{
				postRestRequest.addJsonPart(partEntry.getName(), partEntry.getValue());
			}
		}
		
		File file = null;
		
		//add attachments
		exeLogger.debug("Adding attachments with names: {}", attachments.keySet());
		
		IResource resource = null;
		
		for(Map.Entry<String, String> attachEntry : attachments.entrySet())
		{
			try
			{
				resource = ResourceFactory.getResource(attachEntry.getValue(), exeLogger);
			}catch(Exception ex)
			{
				throw new TestCaseFailedException("An error occurred while loading resource: {}", attachEntry.getValue(), ex);
			}
			
			file = File.createTempFile(attachEntry.getKey(), ".tmp");
			
			FileOutputStream fos = new FileOutputStream(file);
			IOUtils.copy(resource.getInputStream(), fos);
			
			fos.flush();
			fos.close();
			
			postRestRequest.addAttachment(attachEntry.getKey(), file, null);
		}
		
		super.populate(context, postRestRequest, exeLogger);
		super.invoke(context, postRestRequest, exeLogger);
		
		return true;
	}

}
