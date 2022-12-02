package com.yukthitech.autox.debug.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Used when execution is paused because of debug point.
 * @author akranthikiran
 */
public class ServerMssgExecutionPaused implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Represent stack element in stack trace.
	 * 
	 * @author akranthikiran
	 */
	public static class StackElement implements Serializable
	{
		private static final long serialVersionUID = 1L;

		private String file;
		
		private int lineNumber;

		public StackElement(String file, int lineNumber)
		{
			this.file = file;
			this.lineNumber = lineNumber;
		}

		public String getFile()
		{
			return file;
		}

		public int getLineNumber()
		{
			return lineNumber;
		}
	}
	
	/**
	 * Unique id representing current step or execution.
	 */
	private String executionId;
	
	/**
	 * Path of debug point file, where execution got paused.
	 */
	private String debugFilePath;
	
	/**
	 * Line number of debug point file, where execution got paused.
	 */
	private int lineNumber;
	
	/**
	 * Stack trace of current execution.
	 */
	private List<StackElement> stackTrace;
	
	private Map<String, byte[]> contextAttr;

	public ServerMssgExecutionPaused(String executionId, String debugFilePath, int lineNumber, List<StackElement> stackTrace, Map<String, byte[]> contextAttr)
	{
		this.executionId = executionId;
		this.debugFilePath = debugFilePath;
		this.lineNumber = lineNumber;
		this.stackTrace = new ArrayList<>(stackTrace);
		this.contextAttr = contextAttr;
	}

	public String getExecutionId()
	{
		return executionId;
	}

	public String getDebugFilePath()
	{
		return debugFilePath;
	}

	public int getLineNumber()
	{
		return lineNumber;
	}

	public List<StackElement> getStackTrace()
	{
		return stackTrace;
	}

	public Map<String, byte[]> getContextAttr()
	{
		return contextAttr;
	}
}
