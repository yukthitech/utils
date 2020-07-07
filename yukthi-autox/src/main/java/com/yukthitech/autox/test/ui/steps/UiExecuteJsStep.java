package com.yukthitech.autox.test.ui.steps;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.ccg.xml.util.ValidateException;

/**
 * Can be used to execute js code.
 * @author akiran
 */
@Executable(name = "uiExecuteJs", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, 
	message = "Can be used to execute js code. If the result needs to be set on context, from js code 'return' should be used to return approp value.")
public class UiExecuteJsStep extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Script to execute.
	 */
	@Param(description = "Script to execute")
	private String script;
	
	/**
	 * If specified, the result of the js execution (returned by 'return' statement) will be stored with this name on context. Default: null.
	 */
	@Param(description = "If specified, the result of the js execution (returned by 'return' statement) will be stored with this name on context. Default: null", required = false,
			attrName = true)
	private String resultAttribute;

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
	 * Sets the if specified, the result of the js execution (returned by 'return' statement) will be stored with this name on context. Default: null.
	 *
	 * @param resultAttribute the new if specified, the result of the js execution (returned by 'return' statement) will be stored with this name on context
	 */
	public void setResultAttribute(String resultAttribute)
	{
		this.resultAttribute = resultAttribute;
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
		
		Object res = ((JavascriptExecutor)driver).executeScript(script);
		
		if(resultAttribute != null)
		{
			exeLogger.debug("Setting the result of JS execution as context attribute with name '{}'. Result: {}", resultAttribute, res);
			context.setAttribute(resultAttribute, res);
		}
		
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
