package com.yukthitech.automation.logmon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Local file monitor to monitor the changes in local file.
 * @author akiran
 */
public class FileLogMonitor extends AbstractLogMonitor implements Validateable
{
	private static Logger logger = LogManager.getLogger(FileLogMonitor.class);
	
	/**
	 * Path of file to monitor.
	 */
	private String path;
	
	/**
	 * Internal field to track start position at start of monitoring.
	 */
	private long startPosition = -1;
	
	/**
	 * Sets the path of file to monitor.
	 *
	 * @param path the new path of file to monitor
	 */
	public void setPath(String path)
	{
		this.path = path;
	}
	
	@Override
	public void startMonitoring()
	{
		File file = new File(path);
		
		if(!file.exists() || !file.isFile())
		{
			logger.warn("No file found under specified path at start of monitoring. Assuming the file will be created. Path: " + path);
			startPosition = 0;
			return;
		}
		
		startPosition = file.length();
	}

	@Override
	public File stopMonitoring()
	{
		File file = new File(path);
		
		if(!file.exists() || !file.isFile())
		{
			logger.warn("No file found under specified path at end of monitoring. Ignoring monitoring request. Path: " + path);
			return null;
		}
		
		File tempFile = null;
		
		try
		{
			tempFile = File.createTempFile("file-monitoring", ".log");
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while creating temp file");
		}

		long currentSize = file.length();
		
		//if there is no content simply return empty file.
		if(currentSize == 0)
		{
			return tempFile;
		}
		
		//if current size is less than start size
		//	which can happen in rolling logs, read the current file from starting
		if(currentSize < startPosition)
		{
			startPosition = 0;
		}
		
		//calculate amount of log to be read
		long dataToRead = currentSize - startPosition;
		
		try
		{
			RandomAccessFile inputFile = new RandomAccessFile(file, "r");
			inputFile.seek(startPosition);
			
			byte buff[] = new byte[2048];
			int read = 0;
			FileOutputStream fos = new FileOutputStream(tempFile);
			long totalRead = 0;
			
			while( (read = inputFile.read(buff)) > 0)
			{
				fos.write(buff, 0, buff.length);
				totalRead += read;
				
				//once the all content is read till stop monitoring was requested
				if(totalRead >= dataToRead)
				{
					//stop reading
					break;
				}
			}
			
			fos.close();
			inputFile.close();
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while creating monitoring log.");
		}
		
		return tempFile;
	}

	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(path))
		{
			throw new ValidateException("Path can not be null or empty");
		}
	}

}
