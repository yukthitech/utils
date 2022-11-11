package com.yukthitech.autox.debug.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Used when execution is paused because of debug point.
 * @author akranthikiran
 */
public class ExecutionPausedServerMssg implements Serializable
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
	 * Unique id which should be used when the current execution which is paused
	 * has to be moved to next step.
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

	public ExecutionPausedServerMssg(String debugFilePath, int lineNumber, List<StackElement> stackTrace)
	{
		this.debugFilePath = debugFilePath;
		this.lineNumber = lineNumber;
		this.stackTrace = new ArrayList<>(stackTrace);
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
}
