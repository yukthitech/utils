package com.yukthitech.autox;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Controller using which execution can be controlled.
 * @author akiran
 */
public class InteractiveExecutionController
{
	/**
	 * List of actions.
	 * @author akiran
	 */
	public static enum Action
	{
		/**
		 * Indicates execution should be stopped at this point.
		 */
		STOP_EXECUTION,
		
		/**
		 * Indicates execution should continue in normal way.
		 */
		CONTINUE_EXECUTION
		;
	}
	
	/**
	 * Represents a point which needs some action by this controller.
	 * @author akiran
	 */
	private static class FilePoint
	{
		/**
		 * Line number in which this point is set.
		 */
		int lineNumber;

		public FilePoint(int lineNumber)
		{
			this.lineNumber = lineNumber;
		}
	}
	
	/**
	 * End points where execution should stop.
	 */
	private Map<File, FilePoint> endPoints = new HashMap<>();
	
	/**
	 * Adds end point where execution should stop.
	 * @param file
	 * @param lineNo
	 */
	public void addEndPoint(File file, int lineNo)
	{
		try
		{
			file = file.getCanonicalFile();
			this.endPoints.put(file, new FilePoint(lineNo));
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while determining cannoical path from file path: {}", file, ex);
		}
	}
	
	/**
	 * Called by executor before every step execution.
	 * @param file file in which next step being executed is defined
	 * @param lineNo line in which next step being executed is defined
	 * @return action to be performed by executor
	 */
	public Action getAction(File file, int lineNo)
	{
		FilePoint filePoint = null;

		try
		{
			filePoint = this.endPoints.get(file.getCanonicalFile());
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while determining cannoical path from file path: {}", file, ex);
		}
		
		if(filePoint != null && lineNo > filePoint.lineNumber)
		{
			return Action.STOP_EXECUTION;
		}
		
		return Action.CONTINUE_EXECUTION;
	}
}
