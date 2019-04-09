package com.yukthitech.autox.test.ui.steps;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.ccg.xml.util.ValidateException;

/**
 * Can be used to execute js code.
 * @author akiran
 */
@Executable(name = {"uiExecuteJs"}, requiredPluginTypes = SeleniumPlugin.class, message = "Can be used to execute js code")
public class UiExecuteJs extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Script to execute.
	 */
	@Param(description = "Script to execute")
	private String script;

	/**
	 * Sets the script to execute.
	 *
	 * @param script the new script to execute
	 */
	public void setScript(String script)
	{
		if(StringUtils.isBlank(script))
		{
			throw new InvalidArgumentException("Script can not be empty");
		}
		
		this.script = script;
	}
	
	/**
	 * Simulates the click event on the specified button.
	 * @param context Current automation context 
	 */
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.trace("Executing JS script - {}", script);
		
		
		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		WebDriver driver = seleniumConfiguration.getWebDriver();
		
		((JavascriptExecutor)driver).executeScript(script);
		return true;
	}
	
	@Override
	public void validate() throws ValidateException
	{
		super.validate();
		
		if(StringUtils.isBlank(script))
		{
			throw new ValidateException("Script cannot be empty.");
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Script: ").append(script);

		builder.append("]");
		return builder.toString();
	}
}
