package com.yukthitech.automation.test.log;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Log data of the test case.
 */
public class ExecutionLogData
{
	
	/**
	 * Represent log message information.
	 */
	public static class Message
	{
		/**
		 * Log level at which message is logged.
		 */
		private LogLevel logLevel;
		
		/**
		 * Log message.
		 */
		private String message;
		
		/**
		 * Time at which message is logged.
		 */
		private Date time;

		/**
		 * Instantiates a new message.
		 *
		 * @param logLevel the log level
		 * @param message the message
		 * @param time the time
		 */
		public Message(LogLevel logLevel, String message, Date time)
		{
			this.logLevel = logLevel;
			this.message = message;
			this.time = time;
		}

		/**
		 * Gets the log level at which message is logged.
		 *
		 * @return the log level at which message is logged
		 */
		public LogLevel getLogLevel()
		{
			return logLevel;
		}

		/**
		 * Gets the log message.
		 *
		 * @return the log message
		 */
		public String getMessage()
		{
			return message;
		}

		/**
		 * Gets the time at which message is logged.
		 *
		 * @return the time at which message is logged
		 */
		@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
		public Date getTime()
		{
			return time;
		}
		
		/**
		 * Copies required resources to output folder.
		 * @param outFolder folder to copy
		 */
		public void copyResources(File outFolder)
		{}
	}
	
	public static class ImageMessage extends Message
	{
		/**
		 * Name of the image.
		 */
		private String name;
		
		/**
		 * Image file.
		 */
		private File imageFile;
		
		/**
		 * Instantiates a new image message.
		 *
		 * @param logLevel the log level
		 * @param message the message
		 * @param time the time
		 * @param imageFile the image file
		 */
		public ImageMessage(LogLevel logLevel, String message, Date time, String name, File imageFile)
		{
			super(logLevel, message, time);
			
			this.name = name;
			this.imageFile = imageFile;
		}
		
		/**
		 * Gets the image file.
		 *
		 * @return the image file
		 */
		@JsonIgnore
		public File getImageFile()
		{
			return imageFile;
		}
		
		/**
		 * Fetches the target image file name.
		 * @return image file name.
		 */
		public String getImageFileName()
		{
			return imageFile.getName();
		}

		/**
		 * Copies required resources to output folder.
		 * @param outFolder folder to copy
		 */
		public void copyResources(File outFolder)
		{
			File copy = new File(outFolder, name);
			
			try
			{
				FileUtils.copyFile(imageFile, copy);
				
				imageFile.delete();
				this.imageFile = copy;
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while copying image fiel {} to {}", imageFile.getPath(), copy.getPath());
			}
		}
	}
	
	/**
	 * Name of the executor.
	 */
	private String executorName;
	
	/**
	 * Executor description.
	 */
	private String executorDescription;
	
	/**
	 * Messages or sub loggers.
	 */
	private List<Message> messages = new ArrayList<>();

	/**
	 * Instantiates a new execution log data.
	 *
	 * @param executorName the executor name
	 * @param executorDescription the executor description
	 * @param executorType the executor type
	 */
	public ExecutionLogData(String executorName, String executorDescription)
	{
		this.executorName = executorName;
		this.executorDescription = executorDescription;
	}

	/**
	 * Gets the name of the executor.
	 *
	 * @return the name of the executor
	 */
	public String getExecutorName()
	{
		return executorName;
	}

	/**
	 * Gets the executor description.
	 *
	 * @return the executor description
	 */
	public String getExecutorDescription()
	{
		return executorDescription;
	}

	/**
	 * Gets the messages or sub loggers.
	 *
	 * @return the messages or sub loggers
	 */
	public List<Message> getMessages()
	{
		return messages;
	}
	
	/**
	 * Adds the specified message to the log data.
	 * @param message
	 */
	public void addMessage(Message message)
	{
		this.messages.add(message);
	}
	
	/**
	 * Copies required resources to output folder.
	 * @param outFolder folder to copy
	 */
	public void copyResources(File outFolder)
	{
		for(Message message : messages)
		{
			message.copyResources(outFolder);
		}
	}
}
