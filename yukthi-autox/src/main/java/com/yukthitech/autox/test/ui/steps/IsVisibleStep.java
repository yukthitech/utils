package com.yukthitech.autox.test.ui.steps;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.test.ui.common.UiFreeMarkerMethods;

/**
 * Waits for locator to be part of the page and is visible.
 * @author akiran
 */
@Executable(name = "uiIsVisible", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Fetches flag indicating if target element is visible or not")
public class IsVisibleStep extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Locator of the element for which value needs to be fetched.
	 */
	@Param(description = "Locator of the element for which value needs to be fetched", sourceType = SourceType.UI_LOCATOR)
	private String locator;
	
	/**
	 * Name of attribute to set.
	 */
	@Param(description = "Name of the attribute to set.")
	private String name;

	/**
	 * Sets the locator of the element for which value needs to be fetched.
	 *
	 * @param locator the new locator of the element for which value needs to be fetched
	 */
	public void setLocator(String locator)
	{
		this.locator = locator;
	}

	/**
	 * Sets the name of attribute to set.
	 *
	 * @param name the new name of attribute to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Simulates the click event on the specified button.
	 * @param context Current automation context 
	 */
	@Override
	public void execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.trace("Fetching visibility flag for element '{}'", getLocatorWithParent(locator));

		boolean visible = UiFreeMarkerMethods.uiIsVisible(locator, parentElement);
		
		exeLogger.debug("Element '{}' visibility flag ({}) is being set as context attribute: {}", getLocatorWithParent(locator), visible, name);
		context.setAttribute(name, visible);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Is Visible [");

		builder.append("Locator: ").append(locator);
		builder.append(",").append("Name: ").append(name);

		builder.append("]");
		return builder.toString();
	}
}
