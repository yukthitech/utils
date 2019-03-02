package com.yukthitech.autox;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
		 * File in which this point is set.
		 */
		File file;

		/**
		 * Line number in which this point is set.
		 */
		int lineNumber;

		public FilePoint(File file, int lineNumber)
		{
			this.file = file;
			this.lineNumber = lineNumber;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if(obj == this)
			{
				return true;
			}

			if(!(obj instanceof InteractiveExecutionController.FilePoint))
			{
				return false;
			}

			InteractiveExecutionController.FilePoint other = (InteractiveExecutionController.FilePoint) obj;
			return Objects.equals(file, other.file) && (lineNumber == other.lineNumber);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashcode()
		 */
		@Override
		public int hashCode()
		{
			return Objects.hash(file, lineNumber);
		}
	}
	
	/**
	 * End points where execution should stop.
	 */
	private Set<FilePoint> endPoints = new HashSet<>();
	
	/**
	 * Adds end point where execution should stop.
	 * @param file
	 * @param lineNo
	 */
	public void addEndPoint(File file, int lineNo)
	{
		try
		{
			this.endPoints.add(new FilePoint(file.getCanonicalFile(), lineNo));
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
		FilePoint curFilePoint = new FilePoint(file, lineNo);
		
		if(endPoints.contains(curFilePoint))
		{
			return Action.STOP_EXECUTION;
		}
		
		return Action.CONTINUE_EXECUTION;
	}
}
