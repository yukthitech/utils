package com.yukthitech.automation.test.ui.steps;

import java.io.File;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.yukthitech.automation.AbstractStep;
import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.ExecutionLogger;
import com.yukthitech.automation.Param;
import com.yukthitech.automation.config.SeleniumPlugin;

/**
 * Takes the screen shot of the browser and adds it to the log .
 * @author akiran
 */
@Executable(name = "logScreenShot", requiredPluginTypes = SeleniumPlugin.class, message = "Takes current screen snapshot and adds to the log")
public class LogScreenShotStep extends AbstractStep
{
	/**
	 * Name of the file provided by the user.
	 */
	@Param(description = "Name of the screenshot image file to be created")
	private String name;
	
	/**
	 * Message to be logged along with image;
	 */
	@Param(description = "Message to be logged along with image", required = false)
	private String message;

	/**
	 * Sets the name of the file provided by the user.
	 *
	 * @param fileName the new name of the file provided by the user
	 */
	public void setName(String fileName) 
	{
		this.name = fileName;
	}
	
	/**
	 * Sets the message to be logged along with image;.
	 *
	 * @param message the new message to be logged along with image;
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	@Override
	public void execute(AutomationContext context, ExecutionLogger exeLogger) 
	{
		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		WebDriver driver = seleniumConfiguration.getWebDriver();
	
		File file = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		exeLogger.logImage(name, message, file);
	}
}
