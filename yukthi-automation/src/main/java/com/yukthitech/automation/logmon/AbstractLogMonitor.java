package com.yukthitech.automation.logmon;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Base abstract for log monitors which provides functionality to manage name.
 */
public abstract class AbstractLogMonitor implements ILogMonitor, Validateable
{
	/**
	 * Name of the log monitor.
	 */
	private String name;

	/* (non-Javadoc)
	 * @see com.yukthitech.automation.logmon.ILogMonitor#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the log monitor.
	 *
	 * @param name the new name of the log monitor
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(name))
		{
			throw new ValidateException("No/empty name specified for log monitor.");
		}
	}
}
