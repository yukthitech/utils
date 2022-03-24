package com.yukthitech.autox.test.rest.steps;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.config.RestPlugin;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.rest.GetRestRequest;
import com.yukthitech.utils.rest.HttpResponse;
import com.yukthitech.utils.rest.IRestResponseHandler;
import com.yukthitech.utils.rest.RestResult;

/**
 * Used to invoke GET REST API.
 * @author akiran
 */
@Executable(name = "restInvokeGetFile", group = Group.Rest_Api, requiredPluginTypes = RestPlugin.class, message = "Used to invoke GET api and save response as file.")
public class InvokeGetFileStep extends AbstractRestStep
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(InvokeGetFileStep.class);
	
	static class FileResultHandler implements IRestResponseHandler<RestResult<String>>
	{
		private File outFile;
		private ExecutionLogger exeLogger;
		
		public FileResultHandler(File outFile, ExecutionLogger logger)
		{
			this.outFile = outFile;
			this.exeLogger = logger;
		}

		public RestResult<String> handleResponse(HttpResponse response) throws IOException
		{
			int status = response.getCode();
			byte value[] = null;
	
			logger.debug("Got response-status as {}", status);
			
			HttpEntity entity = response.getEntity();

			try
			{
				value = entity != null? EntityUtils.toByteArray(entity): null;
			}catch(Exception ex)
			{
				logger.warn("An error occurred while fetching response content", ex);
				value = null;
			}
			
			logger.debug("Got response status as {} and body as: {}", status, value);
			
			RestResult<String> result = null;
			
			if(value != null)
			{
				exeLogger.debug("Writing content from response to specified file: {}", outFile.getPath());
				FileUtils.writeByteArrayToFile(outFile, value);
				
				result = new RestResult<String>(outFile.getAbsolutePath(), status, response);
			}
			else
			{
				exeLogger.warn("No conent found to write to file");
				result = new RestResult<String>(null, status, response);
			}
			
			Header headers[] =  response.getHeaders();
			
			if(headers != null)
			{
				for(Header header : headers)
				{
					result.addHeader(header.getName(), header.getValue());
				}
			}
			
			return result;
		}
	}
	
	/**
	 * Output file where response content should be stored. If not specified, temp file will be used. The output file path will be set response attribute.
	 */
	@Param(description = "Output file where response content should be stored. If not specified, temp file will be used. The output file path will be set as rest-result value", required = false)
	private String outputFile;

	/**
	 * Sets the output file where response content should be stored. If not specified, temp file will be used. The output file path will be set response attribute.
	 *
	 * @param outputFile the new output file where response content should be stored
	 */
	public void setOutputFile(String outputFile)
	{
		this.outputFile = outputFile;
	}
	
	@Override
	public void execute(AutomationContext context, ExecutionLogger logger) throws Exception
	{
		GetRestRequest getRestRequest = new GetRestRequest(uri);
		
		super.populate(context, getRestRequest, logger);
		super.invoke(context, getRestRequest, logger);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected IRestResponseHandler<RestResult<?>> getRestResultHandler(ExecutionLogger exeLogger)
	{
		File file = null;
		
		try
		{
			file = outputFile != null ? new File(outputFile) : File.createTempFile("response", ".tmp");
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while creating file", ex);
		}
		
		return (IRestResponseHandler) new FileResultHandler(file, exeLogger);
	}
}
