package com.yukthitech.autox.test.ui.steps;

import java.io.File;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.test.log.LogLevel;

/**
 * Takes the screen shot of the browser and adds it to the log .
 * @author akiran
 */
@Executable(name = {"uiLogScreenShot", "logScreenShot"}, requiredPluginTypes = SeleniumPlugin.class, message = "Takes current screen snapshot and adds to the log")
public class LogScreenShotStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

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
	 * Logging level.
	 */
	@Param(description = "Logging level. Default Value: DEBUG", required = false)
	private LogLevel level = LogLevel.DEBUG;

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
	
	/**
	 * Sets the logging level.
	 *
	 * @param level the new logging level
	 */
	public void setLevel(LogLevel level)
	{
		this.level = level;
	}

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger) 
	{
		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		WebDriver driver = seleniumConfiguration.getWebDriver();
	
		File file = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		exeLogger.logImage(this, name, message, file, level);
		
		return true;
	}
}
