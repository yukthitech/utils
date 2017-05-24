package com.yukthitech.autox.test.rest.steps;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.config.RestPlugin;
import com.yukthitech.autox.resource.IResource;
import com.yukthitech.autox.resource.ResourceFactory;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.rest.PutRestRequest;

/**
 * Used to invoke Multi part POST REST API.
 * @author akiran
 */
@Executable(name = "invokeMultipartPut", requiredPluginTypes = RestPlugin.class, message = "Used to invoke Multipart PUT api.")
public class InvokePutWithAttachmentStep extends AbstractRestStep implements Validateable
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Parts to be set on the request. If non-string is specified, object will be converted to json and content-type of part will be set as JSON.
	 */
	@Param(description = "Parts to be set on the request. If non-string is specified, object will be converted to json and content-type of part will be set as JSON.", required = false, sourceType = SourceType.RESOURCE)
	private List<HttpPart> parts = new ArrayList<>();
	
	@Param(description = "List of files to be attachment with this request. Values are resources", required = false, sourceType = SourceType.RESOURCE)
	private List<HttpAttachment> attachments = new ArrayList<>();

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
	public void addAttachment(HttpAttachment attachment)
	{
		attachments.add(attachment);
	}
	
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger) throws Exception
	{
		PutRestRequest putRestRequest = new PutRestRequest(uri);
		putRestRequest.setMultipartRequest(true);
		
		IResource partResource = null;
		
		for(HttpPart partEntry : parts)
		{
			exeLogger.debug("Adding part with name: {}", partEntry.getName());
			
			if(partEntry.getValue() instanceof String)
			{
				partResource = ResourceFactory.getResource(context, (String) partEntry.getValue(), exeLogger, true);
				putRestRequest.addTextPart(partEntry.getName(), partResource.toText(), partEntry.getContentType());
			}
			else
			{
				putRestRequest.addJsonPart(partEntry.getName(), partEntry.getValue());
			}
		}
		
		File file = null;
		
		//add attachments
		IResource resource = null;
		List<File> filesToDelete = new ArrayList<>();
		
		for(HttpAttachment attachment : attachments)
		{
			exeLogger.debug("Adding attachment with name: {}", attachment.getName());
			
			resource = ResourceFactory.getResource(context, attachment.getFile(), exeLogger, attachment.isParseAsTemplate());
			
			file = File.createTempFile(attachment.getName(), ".tmp");
			
			FileOutputStream fos = new FileOutputStream(file);
			IOUtils.copy(resource.getInputStream(), fos);
			
			fos.flush();
			fos.close();
			
			putRestRequest.addAttachment(attachment.getName(), file, null);
			filesToDelete.add(file);
		}
		
		super.populate(context, putRestRequest, exeLogger);
		super.invoke(context, putRestRequest, exeLogger);
		
		for(File fileToDel : filesToDelete)
		{
			if(!fileToDel.delete())
			{
				exeLogger.debug("Failed to delete temp file: {}", fileToDel.getPath());
			}
		}
		
		return true;
	}

}
