package com.yukthitech.autox.logmon;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.Logs;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Local file monitor to monitor the changes in local file.
 * @author akiran
 */
public class BrowserLogMonitor extends AbstractLogMonitor implements Validateable
{
	private static Logger logger = LogManager.getLogger(BrowserLogMonitor.class);
	
	/**
	 * Web driver captured during last start monitoring.
	 */
	private WebDriver currentWebDriver;
	
	/**
	 * Selenium logs object that is captured during last invocation.
	 */
	private Logs currentLogs;
	
	/**
	 * Format used to print time in logs.
	 */
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
	
	/**
	 * Sets the format used to print time in logs.
	 *
	 * @param dateFormat the new format used to print time in logs
	 */
	public void setDateFormat(String dateFormat)
	{
		this.dateFormat = new SimpleDateFormat(dateFormat);
	}
	
	@Override
	public void startMonitoring(AutomationContext context)
	{
		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		
		if(seleniumConfiguration == null)
		{
			logger.warn("As selenium-plugin is enabled, the request for monitoring browser-log is ignored.");
			return;
		}
		
		WebDriver driver = seleniumConfiguration.getWebDriver();
		
		if(currentWebDriver != driver || currentLogs == null)
		{
			try
			{
				currentLogs = driver.manage().logs();
			}catch(Exception ex)
			{
				logger.error("An error occurred while fetching logs object from web-driver", ex);
				return;
			}
			
			if(currentLogs == null)
			{
				logger.warn("As no logs object could be obtained from webdriver, request for monitoring browser logs ignored");
				return;
			}
			
			currentWebDriver = driver;
		}
		
		//this will clean the browser logs available till now
		try
		{
			currentLogs.get(LogType.BROWSER);
		}catch(Exception ex)
		{
			logger.debug("Ignoring error that occurred, while trying to cleanup browser logs, during start of log monitor");
		}
	}

	@Override
	public List<LogFile> stopMonitoring(AutomationContext context)
	{
		if(currentLogs == null)
		{
			logger.warn("As current logs object is not available for webdriver, no log file is being generated.");
			return null;
		}
		
		
		File tempFile = null;
		
		try
		{
			tempFile = File.createTempFile("file-monitoring", ".log");
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while creating temp file");
		}

		
		try
		{
			LogEntries logEntries = currentLogs.get(LogType.BROWSER);
			
			if(logEntries == null)
			{
				logger.debug("As there is no log entries, returning null from this log monitor");
				return null;
			}

			StringBuilder builder = new StringBuilder();
			String template = "%s [%s] - %s";
			String mssg = null;

			for (LogEntry entry : logEntries) 
			{
				mssg = String.format(template, entry.getLevel().getName(), dateFormat.format(new Date(entry.getTimestamp())), entry.getMessage());
				builder.append(mssg).append("\n");
			}
			
			if(builder.length() == 0)
			{
				logger.debug("As there is no content, returning null from this log monitor");
				return null;
			}

			FileUtils.write(tempFile, mssg);
		}catch(Exception ex)
		{
			logger.error("An error occurred while creating monitoring log.", ex);
			return null;
		}
		
		return Arrays.asList(new LogFile(super.getName(), tempFile));
	}
}
