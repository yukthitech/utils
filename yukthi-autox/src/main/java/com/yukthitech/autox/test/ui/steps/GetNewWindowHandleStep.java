package com.yukthitech.autox.test.ui.steps;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Helps in switching between windows.
 * 
 * @author akiran
 */
@Executable(name = {"uiGetNewWindowHandle"}, requiredPluginTypes = SeleniumPlugin.class, message = "Used to fetch newly opened window handle.")
public class GetNewWindowHandleStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name by which window can be accessed.
	 */
	@Param(description = "Name by which window can be accessed.")
	private String name;
	
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.trace(this, "Fetching new window handle and naming it as - {}", name);

		String handle = UiAutomationUtils.getNewWindowHandle(context);
		
		
		return true;
	}

	/**
	 * Sets the name by which window can be accessed.
	 *
	 * @param name the new name by which window can be accessed
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Name: ").append(name);

		builder.append("]");
		return builder.toString();
	}
}
