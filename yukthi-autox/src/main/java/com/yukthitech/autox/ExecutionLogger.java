package com.yukthitech.autox;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.monitor.MonitorLogMessage;
import com.yukthitech.autox.test.log.ExecutionLogData;
import com.yukthitech.autox.test.log.LogLevel;

/**
 * A simple internal logger to consolidate execution messages in test result.
 * 
 * @author akiran
 */
public class ExecutionLogger
{
	private static Logger logger = LogManager.getLogger(ExecutionLogger.class);

	private static int fileIndex = 1;
	
	private static final Pattern PARAM_PATTERN = Pattern.compile("\\{(\\d*)\\}");
	
	/**
	 * Context as part of which this logger is created.
	 */
	private AutomationContext automationContext;
	
	/**
	 * Log data information.
	 */
	private ExecutionLogData executionLogData;
	
	/**
	 * Flag indicating if logging is disabled or not.
	 */
	private boolean disabled = false;
	
	/**
	 * Mode to be prepended for every log message.
	 */
	private String mode;

	public ExecutionLogger(AutomationContext automationContext, String executorName, String executorDescription)
	{
		this.automationContext = automationContext;
		this.executionLogData = new ExecutionLogData(executorName, executorDescription);
	}
	
	/**
	 * Sets the mode to be prepended for every log message.
	 *
	 * @param mode the new mode to be prepended for every log message
	 */
	public void setMode(String mode)
	{
		this.mode = mode;
	}
	
	/**
	 * Clears the mode from the logger.
	 */
	public void clearMode()
	{
		this.mode = null;
	}
	
	/**
	 * Sets the flag indicating if logging is disabled or not.
	 *
	 * @param disabled the new flag indicating if logging is disabled or not
	 */
	public void setDisabled(boolean disabled)
	{
		this.disabled = disabled;
	}
	
	/**
	 * Gets the flag indicating if logging is disabled or not.
	 *
	 * @return the flag indicating if logging is disabled or not
	 */
	public boolean isDisabled()
	{
		return disabled;
	}
	
	private String getSource(StackTraceElement stackTrace[])
	{
		return stackTrace[2].getFileName() + ":" + stackTrace[2].getLineNumber();
	}
	
	/**
	 * Fetches the location of the element.
	 * @param locationBased
	 * @return
	 */
	private String getSourceLocation()
	{
		return automationContext.getExecutionStack().getCurrentLocation();
	}
	
	/**
	 * Replaces the args values in "message" using patterns mentioned below and same will be returned. 
	 * 
	 * {} will match with the current index argument. If index is greater than provided values then &lt;undefined&gt; string will be used.
	 * {&lt;idx&gt;} can be used to refer to argument at particular index. Helpful in building messages which uses same argument multiple times.
	 * 
	 * @param message Message string with expressions
	 * @param args Values for expression
	 * @return Formatted string
	 */
	public static String format(String message, Object... args)
	{
		//when message is null, return null
		if(message == null)
		{
			return null;
		}
		
		//when args is null, assume empty values
		if(args == null)
		{
			args = new Object[0];
		}
		
		Matcher matcher = PARAM_PATTERN.matcher(message);
		StringBuffer buffer = new StringBuffer();
		
		int loopIndex = 0;
		int argIndex = 0;
		Object arg = null;
		
		//loop through pattern matches
		while(matcher.find())
		{
			//if index is mentioned in pattern
			if(org.apache.commons.lang3.StringUtils.isNotBlank(matcher.group(1)))
			{
				argIndex = Integer.parseInt(matcher.group(1));
			}
			//if index is not specified, use current loop index
			else
			{
				argIndex = loopIndex;
			}
			
			//if the index is within provided arguments length
			if(argIndex < args.length)
			{
				arg = args[argIndex];
			}
			//if the index is greater than available values
			else
			{
				arg = "<undefined>";
			}
			
			String argStr = null;
			
			//if argument value is null
			if(arg == null)
			{
				argStr = "null";
			}
			else
			{
				argStr = arg.toString();
				argStr = (argStr.length() > 1000) ? (argStr.substring(0, 1000) + "...") : argStr;
			}

			argStr = Matcher.quoteReplacement(argStr);
			
			matcher.appendReplacement(buffer, argStr);
			loopIndex++;
		}
		
		matcher.appendTail(buffer);
		return buffer.toString();
	}

	private String buildMessage(boolean escapeHtml, String mssgTemplate, Object... args)
	{
		String finalMssg = format(mssgTemplate, args);
		
		if(escapeHtml)
		{
			finalMssg = StringEscapeUtils.escapeHtml4(finalMssg);
		}

		if(mode != null)
		{
			finalMssg = "<b>[" + mode + "]</b> " + finalMssg;
		}
		
		return finalMssg;
	}
	
	private void addMessage(ExecutionLogData.Message mssg)
	{
		executionLogData.addMessage(mssg);
		
		automationContext.sendAsyncMonitorMessage(new MonitorLogMessage(
				automationContext.getActiveTestSuite() != null ? automationContext.getActiveTestSuite().getName() : null, 
				automationContext.getActiveTestCase() != null ? automationContext.getActiveTestCase().getName() : null, 
				automationContext.getActiveTestCaseData() != null ? automationContext.getActiveTestCaseData().getName() : null, 
				mssg, 
				automationContext.isSetupExecution(),
				automationContext.isCleanupExecution())
		);
	}

	/**
	 * Used to log error messages as part of current execution.
	 * @param escapeHtml Whether html tags should be escaped in result content.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	public void error(boolean escapeHtml, String mssgTemplate, Object... args)
	{
		error(escapeHtml, null, mssgTemplate, args);
	}

	/**
	 * Used to log error messages as part of current execution.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	public void error(String mssgTemplate, Object... args)
	{
		error(true, null, mssgTemplate, args);
	}

	/**
	 * Used to log error messages as part of current execution.
	 * @param source location from where logging is being done
	 * @param th Throwable stack trace.
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	public void error(Throwable th, String mssgTemplate, Object... args)
	{
		error(true, th, mssgTemplate, args);
	}
	
	/**
	 * Used to log error messages as part of current execution.
	 * @param escapeHtml Whether html tags should be escaped in result content.
	 * @param source location from where logging is being done
	 * @param th Throwable stack trace.
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	public void error(boolean escapeHtml, Throwable th, String mssgTemplate, Object... args)
	{
		String finalMssg = buildMessage(escapeHtml, mssgTemplate, args);
		
		String autoxStackTrace = automationContext.getExecutionStack().toStackTrace();
		
		logger.error(finalMssg, autoxStackTrace);
		
		if(th != null)
		{
			logger.error(finalMssg, th);
			
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			
			printWriter.println(finalMssg);
			printWriter.println(autoxStackTrace);
			th.printStackTrace(printWriter);
			printWriter.flush();
			
			finalMssg = stringWriter.toString();
		}
		
		addMessage(new ExecutionLogData.Message( getSourceLocation(), getSource(Thread.currentThread().getStackTrace()), LogLevel.ERROR, finalMssg, new Date()));
	}
	
	/**
	 * Used to log debug messages as part of current execution.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	public void debug(String mssgTemplate, Object... args)
	{
		debug(true, mssgTemplate, args);
	}

	/**
	 * Used to log debug messages as part of current execution.
	 * @param escapeHtml Whether html tags should be escaped in result content.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	public void debug(boolean escapeHtml, String mssgTemplate, Object... args)
	{
		if(disabled)
		{
			return;
		}
		
		String finalMssg = buildMessage(escapeHtml, mssgTemplate, args);

		logger.debug(finalMssg);
		addMessage(new ExecutionLogData.Message( getSourceLocation(), getSource(Thread.currentThread().getStackTrace()), LogLevel.DEBUG, finalMssg, new Date()));
	}
	
	/**
	 * Used to log trace messages as part of current execution.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	public void trace(String mssgTemplate, Object... args)
	{
		trace(true, mssgTemplate, args);
	}
	
	/**
	 * Used to log trace messages as part of current execution.
	 * @param escapeHtml Whether html tags should be escaped in result content.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	public void trace(boolean escapeHtml, String mssgTemplate, Object... args)
	{
		if(disabled)
		{
			return;
		}
		
		String finalMssg = buildMessage(escapeHtml, mssgTemplate, args);

		logger.trace(finalMssg);
		addMessage(new ExecutionLogData.Message( getSourceLocation(), getSource(Thread.currentThread().getStackTrace()), LogLevel.TRACE, finalMssg, new Date()));
	}

	/**
	 * Logs the message at specified level.
	 * @param source location from where logging is being done
	 * @param logLevel level of log
	 * @param mssgTemplate msg template
	 * @param args arguments for message
	 */
	public void log(LogLevel logLevel, String mssgTemplate, Object... args)
	{
		log(true, logLevel, mssgTemplate, args);
	}
	
	/**
	 * Logs the message at specified level.
	 * @param escapeHtml Whether html tags should be escaped in result content.
	 * @param source location from where logging is being done
	 * @param logLevel level of log
	 * @param mssgTemplate msg template
	 * @param args arguments for message
	 */
	public void log(boolean escapeHtml, LogLevel logLevel, String mssgTemplate, Object... args)
	{
		if(disabled)
		{
			return;
		}
		
		String finalMssg = buildMessage(escapeHtml, mssgTemplate, args);

		logger.debug(finalMssg);
		addMessage(new ExecutionLogData.Message( getSourceLocation(), getSource(Thread.currentThread().getStackTrace()), logLevel, finalMssg, new Date()));
	}

	/**
	 * Adds the specified image file to the debug log.
	 * @param source location from where logging is being done
	 * @param name Name of the image file for easy identification
	 * @param message Message to be logged along with image
	 * @param imageFile Image to be logged
	 * @param logLevel level to be used.
	 */
	public void logImage(String name, String message, File imageFile, LogLevel logLevel)
	{
		if(disabled)
		{
			return;
		}
		
		if(logLevel == null)
		{
			logLevel = LogLevel.DEBUG;
		}
		
		if(mode != null)
		{
			message = (message != null) ? "" : message;
			message = "<b>[" + mode + "]</b> " + message;
		}
		
		int dotIdx = imageFile.getName().lastIndexOf(".");
		String extension = "";
		
		if(dotIdx > 0)
		{
			extension = imageFile.getName().substring(dotIdx);
		}
		
		if(name != null)
		{
			name = name + "_" + System.currentTimeMillis() + "_" + (fileIndex++) + extension;
		}
		else
		{
			name = System.currentTimeMillis() + "_" + (fileIndex++) + extension;
		}
		
		addMessage(new ExecutionLogData.ImageMessage( getSourceLocation(), getSource(Thread.currentThread().getStackTrace()), logLevel, message, new Date(), name, imageFile));
	}
	
	/**
	 * Gets the log data information.
	 *
	 * @return the log data information
	 */
	public ExecutionLogData getExecutionLogData()
	{
		return executionLogData;
	}
}
