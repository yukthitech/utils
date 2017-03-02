package com.yukthitech.automation.logmon;

/**
 * Base abstract for log monitors which provides functionality to manage name.
 */
public abstract class AbstractLogMonitor implements ILogMonitor
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
}
