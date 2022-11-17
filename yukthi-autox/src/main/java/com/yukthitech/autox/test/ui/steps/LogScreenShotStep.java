package com.yukthitech.autox.test.ui.steps;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.ReportLogFile;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.exec.report.LogLevel;

/**
 * Takes the screen shot of the browser and adds it to the log .
 * @author akiran
 */
@Executable(name = "uiLogScreenShot", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Takes current screen snapshot and adds to the log")
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
	public void execute(AutomationContext context, IExecutionLogger exeLogger) 
	{
		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		WebDriver driver = seleniumConfiguration.getWebDriver();
	
		File file = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		ReportLogFile reportLogFile = context.newLogFile(name, FilenameUtils.getExtension(file.getName()));
		reportLogFile.copyContent(file);
		
		exeLogger.logImage(message, reportLogFile, level);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Log Screenshot [");

		builder.append("Name: ").append(name);

		builder.append("]");
		return builder.toString();
	}

}
