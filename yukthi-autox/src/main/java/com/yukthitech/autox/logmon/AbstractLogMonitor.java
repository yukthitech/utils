package com.yukthitech.autox.logmon;

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
	
	/**
	 * If this flag is set, then only on error, the log will be fetched and added to report.
	 * Defaults to true.
	 */
	private boolean onErrorOnly = true;
	
	/**
	 * Flag indicating whether this log monitor is enabled or not.
	 */
	private boolean enabled = true;

	/**
	 * Gets the name of the log monitor.
	 *
	 * @return the name of the log monitor
	 */
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
	
	/**
	 * Checks if is if this flag is set, then only on error, the log will be fetched and added to report. Defaults to true.
	 *
	 * @return the if this flag is set, then only on error, the log will be fetched and added to report
	 */
	public boolean isOnErrorOnly()
	{
		return onErrorOnly;
	}

	/**
	 * Sets the if this flag is set, then only on error, the log will be fetched and added to report. Defaults to true.
	 *
	 * @param onErrorOnly the new if this flag is set, then only on error, the log will be fetched and added to report
	 */
	public void setOnErrorOnly(boolean onErrorOnly)
	{
		this.onErrorOnly = onErrorOnly;
	}
	
	/**
	 * Checks if is flag indicating whether this log monitor is enabled or not.
	 *
	 * @return the flag indicating whether this log monitor is enabled or not
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * Sets the flag indicating whether this log monitor is enabled or not.
	 *
	 * @param enabled the new flag indicating whether this log monitor is enabled or not
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	/**
	 * Validate.
	 *
	 * @throws ValidateException the validate exception
	 */
	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(name))
		{
			throw new ValidateException("No/empty name specified for log monitor.");
		}
	}
}
