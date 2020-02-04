package com.yukthitech.autox.test.proxy.steps;

/**
 * Wait configuration to be used before sending response.
 * @author akiran
 */
public class WaitConfig
{
	/**
	 * Time to wait before sending response.
	 */
	private long time;
	
	/**
	 * Wait for this attribute to be set.
	 */
	private String forAttr;

	/**
	 * Gets the time to wait before sending response.
	 *
	 * @return the time to wait before sending response
	 */
	public long getTime()
	{
		return time;
	}

	/**
	 * Sets the time to wait before sending response.
	 *
	 * @param time the new time to wait before sending response
	 */
	public void setTime(long time)
	{
		this.time = time;
	}

	/**
	 * Gets the wait for this attribute to be set.
	 *
	 * @return the wait for this attribute to be set
	 */
	public String getForAttr()
	{
		return forAttr;
	}

	/**
	 * Sets the wait for this attribute to be set.
	 *
	 * @param forAttr the new wait for this attribute to be set
	 */
	public void setForAttr(String forAttr)
	{
		this.forAttr = forAttr;
	}
}
