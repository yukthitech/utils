package com.yukthitech.autox.ref;

import com.yukthitech.autox.AutomationContext;

/**
 * Abstraction of object references.
 * @author akiran
 */
public interface IReference
{
	/**
	 * In the specified context fetches value for current reference.
	 * @param context context in which reference value to be fetched
	 * @return reference value of specified context
	 */
	public Object getValue(AutomationContext context);
}
