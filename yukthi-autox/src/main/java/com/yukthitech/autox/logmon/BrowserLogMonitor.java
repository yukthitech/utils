package com.yukthitech.autox.logmon;

import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Local file monitor to monitor the changes in local file.
 * @author akiran
 */
public class BrowserLogMonitor extends AbstractLogMonitor implements Validateable
{
	/**
	 * Format used to print time in logs.
	 */
	String dateFormat = "dd/MM/yyyy hh:mm:ss aa";
	
	/**
	 * Name of the selenium driver to use.
	 */
	String driverName;
	
	/**
	 * Sets the format used to print time in logs.
	 *
	 * @param dateFormat the new format used to print time in logs
	 */
	public void setDateFormat(String dateFormat)
	{
		this.dateFormat = dateFormat;
	}
	
	public void setDriverName(String driverName)
	{
		this.driverName = driverName;
	}
	
	@Override
	public ILogMonitorSession newSession()
	{
		return new BrowserLogMonitorSession(this);
	}
}
