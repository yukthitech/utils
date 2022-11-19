package com.yukthitech.autox.test.ui.assertion;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.Param;

/**
 * Base abstract class for ui validations, which will hold optional parent element.
 * @author akiran
 */
public abstract class AbstractUiAssert extends AbstractValidation
{
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the parent element under which locator needs to be searched. If not specified, fetches globally.
	 */
	@Param(description = "Name of the parent element under which locator needs to be searched. If not specified, fetches globally.", required = false)
	protected String parentElement;
	
	@Param(description = "Name of the driver to be used for the step. Defaults to default driver.", required = false)
	protected String driverName;

	/**
	 * Sets the name of the parent element under which locator needs to be searched. If not specified, fetches globally.
	 *
	 * @param parentElement the new name of the parent element under which locator needs to be searched
	 */
	public void setParentElement(String parentElement)
	{
		this.parentElement = parentElement;
	}
	
	public void setDriverName(String driverName)
	{
		this.driverName = driverName;
	}
	
	/**
	 * Fetches string which includes specified locator and parent locatory, if any.
	 * @param locator locator to format
	 * @return locator string along with parent.
	 */
	protected String getLocatorWithParent(String locator)
	{
		if(parentElement != null)
		{
			return String.format("[Locator: {}, Parent: {}]", locator, parentElement);
		}
		
		return String.format("[Locator: {}]", locator);
	}
}
