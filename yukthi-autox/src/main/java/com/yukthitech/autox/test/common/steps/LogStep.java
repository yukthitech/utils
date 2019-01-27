package com.yukthitech.autox.test.common.steps;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.test.log.LogLevel;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Logs the specified message using execution logger.
 * @author akiran
 */
@Executable(name = "log", message = "Logs specified message. Multiple messages can be specified in single log statement. "
		+ "If non-string or non-primitive values are specified they are converted to json before printing.")
public class LogStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Object mapper to print json from objects.
	 */
	private static ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Logs specified message in ui.
	 */
	@Param(name = "message", description = "Message(s)/object(s) to log", sourceType = SourceType.EXPRESSION)
	private List<Object> messages = new ArrayList<>();
	
	/**
	 * Logging level.
	 */
	@Param(description = "Logging level. Default Value: DEBUG", required = false)
	private LogLevel level = LogLevel.DEBUG;
	
	/**
	 * Sets the logs specified message in ui.
	 *
	 * @param message the new logs specified message in ui
	 */
	public void addMessage(Object message)
	{
		this.messages.add(message);
	}
	
	/**
	 * Sets the logging level.
	 *
	 * @param level the new logging level
	 */
	public void setLevel(LogLevel level)
	{
		this.level = level;
	}
	
	/**
	 * Adds the message to specified builder.
	 * @param message message to be added
	 * @param builder Final result builder
	 */
	private void appendMessage(Object message, StringBuilder builder)
	{
		if(builder.length() > 0)
		{
			builder.append("\n");
		}
		
		if(message == null)
		{
			builder.append("null");
			return;
		}
		
		if(message instanceof String)
		{
			builder.append((String) message);
			return;
		}
		
		if(CommonUtils.isWrapperClass(message.getClass()))
		{
			builder.append("" + message);
			return;
		}
		
		try
		{
			String jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
			builder.append("<code class='JSON'>").append(jsonContent).append("</code>");
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while converting object into json. Object: {}", message, ex);
		}
	}

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger) 
	{
		StringBuilder finalMssg = new StringBuilder();
		
		for(Object message : messages)
		{
			appendMessage(message, finalMssg);
		}
		
		if(level == LogLevel.DEBUG)
		{
			exeLogger.debug(this, finalMssg.toString());
		}
		else if(level == LogLevel.ERROR)
		{
			exeLogger.error(this, finalMssg.toString());
		}
		else if(level == LogLevel.TRACE)
		{
			exeLogger.trace(this, finalMssg.toString());
		}
		else
		{
			exeLogger.log(this, level, finalMssg.toString());
		}
		
		if(level == LogLevel.SUMMARY)
		{
			context.addSumarryMessage(finalMssg.toString());
		}
		
		return true;
	}
}
