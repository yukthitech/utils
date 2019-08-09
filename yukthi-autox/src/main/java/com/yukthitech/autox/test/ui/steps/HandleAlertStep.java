package com.yukthitech.autox.test.ui.steps;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Used to validate and click ok of alert prompt.
 * @author akiran
 */
@Executable(name = "uiHandleAlert", requiredPluginTypes = SeleniumPlugin.class, message = "Used to validate and click ok of alert prompt.")
public class HandleAlertStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Messaged expected in alert. If specified, alert message will be validated with this message..
	 */
	@Param(description = "Messaged expected in alert. If specified, alert message will be validated with this message.", required = false)
	private String expectedMessage;

	/**
	 * Sets the messaged expected in alert. If specified, alert message will be validated with this message..
	 *
	 * @param expectedMessage the new messaged expected in alert
	 */
	public void setExpectedMessage(String expectedMessage)
	{
		this.expectedMessage = expectedMessage;
	}
	
	/**
	 * Simulates the click event on the specified button.
	 * @param context Current automation context 
	 */
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		if(expectedMessage != null)
		{
			exeLogger.debug("Handling alert and validating message to be - '{}'", expectedMessage);
		}
		else
		{
			exeLogger.debug("Handling alert without validation of message..");
		}

		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		WebDriver driver = seleniumConfiguration.getWebDriver();

		Alert alert = driver.switchTo().alert();
		
		if(expectedMessage != null)
		{
			if(expectedMessage.trim().equals(alert.getText().trim()))
			{
				exeLogger.debug("Found alert message to be as expected");
			}
			else
			{
				exeLogger.error("Found alert message '{}' and expected message '{}' are different", alert.getText(), expectedMessage);
				throw new InvalidStateException("Found alert message '{}' and expected message '{}' are different", alert.getText(), expectedMessage);
			}
		}
		
		alert.accept();
		exeLogger.debug("Successfully closed the alert.");
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Expected Message: ").append(expectedMessage);

		builder.append("]");
		return builder.toString();
	}
}
