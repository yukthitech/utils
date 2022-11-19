package com.yukthitech.autox.logmon;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Local file monitor to monitor the changes in local file.
 * @author akiran
 */
public class FileLogMonitor extends AbstractLogMonitor implements Validateable
{
	/**
	 * Path of file to monitor.
	 */
	private String path;
	
	/**
	 * Sets the path of file to monitor.
	 *
	 * @param path the new path of file to monitor
	 */
	public void setPath(String path)
	{
		this.path = path;
	}
	
	String getPath()
	{
		return path;
	}
	
	@Override
	public ILogMonitorSession newSession()
	{
		return new FileLogMonitorSession(this);
	}
	
	@Override
	public void validate() throws ValidateException
	{
		if(!isEnabled())
		{
			return;
		}
		
		super.validate();
		
		if(StringUtils.isBlank(path))
		{
			throw new ValidateException("Path can not be null or empty");
		}
	}

}
