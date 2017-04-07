package com.yukthitech.autox.performance;

import java.util.Date;

/**
 * The Class BeanAutomationContext.
 */
public class BeanAutomationContext
{
	/**
	 * The index.
	 **/
	private int index;

	/**
	 * Gets the index.
	 *
	 * @return the index
	 */
	public int getIndex()
	{
		return index;
	}

	/**
	 * Sets the index.
	 *
	 * @param index
	 *            the new index
	 */
	public void setIndex(int index)
	{
		this.index = index;
	}

	public Date getToday()
	{
		return new Date();
	}
}
