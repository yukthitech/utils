package com.yukthitech.automation.test.ui.steps;

import java.io.File;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.IExecutionLogger;
import com.yukthitech.automation.IStep;
import com.yukthitech.automation.config.SeleniumConfiguration;

@Executable(value = "logScreenShot", requiredConfigurationTypes = SeleniumConfiguration.class, message = "Takes current screen snapshot")
public class ScreenShotStep implements IStep
{
	/**
	 * Name of the file provided by the user.
	 */
	private String name;
	
	/**
	 * Message to be logged along with image;
	 */
	private String message;

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
	public void execute(AutomationContext context, IExecutionLogger exeLogger) 
	{
		SeleniumConfiguration seleniumConfiguration = context.getConfiguration(SeleniumConfiguration.class);
		WebDriver driver = seleniumConfiguration.getWebDriver();
	
		File file = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		exeLogger.logImage(name, message, file);
	}
}
