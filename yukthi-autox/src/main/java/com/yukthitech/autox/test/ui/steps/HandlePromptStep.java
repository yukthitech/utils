package com.yukthitech.autox.test.ui.steps;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Waits for locator to be part of the page and is visible.
 * @author akiran
 */
@Executable(name = "uiHandlePrompt", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Used to validate, feed and accept/cancel prompt.")
public class HandlePromptStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Messaged expected in alert. If specified, alert message will be validated with this message..
	 */
	@Param(description = "Messaged expected in alert. If specified, alert message will be validated with this message.", required = false)
	private String expectedMessage;
	
	/**
	 * If specified, feeds the specified text to the prompt.
	 */
	@Param(description = "If specified, feeds the specified text to the prompt", required = false)
	private String text;
	
	/**
	 * Flag used to accept or cancel confirm box. Default: true.
	 */
	@Param(description = "Flag used to accept or cancel confirm box. Default: true")
	private boolean accept = true;

	/**
	 * Sets the messaged expected in alert. If specified, alert message will be validated with this message..
	 *
	 * @param expectedMessage the new messaged expected in alert
	 */
	public void setExpectedMessage(String expectedMessage)
	{
		this.expectedMessage = expectedMessage;
	}
	
	public void setAccept(boolean accept)
	{
		this.accept = accept;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	/**
	 * Simulates the click event on the specified button.
	 * @param context Current automation context 
	 */
	@Override
	public void execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		if(expectedMessage != null)
		{
			exeLogger.debug("Handling confirm and validating message to be - '{}'", expectedMessage);
		}
		else
		{
			exeLogger.debug("Handling confirm without validation of message..");
		}

		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		WebDriver driver = seleniumConfiguration.getWebDriver();

		Alert alert = driver.switchTo().alert();
		
		if(expectedMessage != null)
		{
			if(expectedMessage.trim().equals(alert.getText().trim()))
			{
				exeLogger.debug("Found confirm message to be as expected");
			}
			else
			{
				exeLogger.error("Found confirm message '{}' and expected message '{}' are different", alert.getText(), expectedMessage);
				throw new InvalidStateException("Found confirm message '{}' and expected message '{}' are different", alert.getText(), expectedMessage);
			}
		}
		
		if(text != null)
		{
			alert.sendKeys(text);
		}
	
		if(accept)
		{
			alert.accept();
			exeLogger.debug("Successfully accepted the confirmation.");
		}
		else
		{
			alert.dismiss();
			exeLogger.debug("Successfully dismissed the confirmation.");
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Handle Prompt [");

		builder.append("Expected Mssg: ").append(expectedMessage);
		builder.append(", ").append("Accept: ").append(accept);
		builder.append(", ").append("Value: ").append(text);

		builder.append("]");
		return builder.toString();
	}
}
