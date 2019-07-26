package com.yukthitech.autox.config.selenium;

import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;

import com.yukthitech.autox.config.SeleniumDriverConfig;

public class AutoxChromeDriver extends ChromeDriver
{
	private static Logger logger = LogManager.getLogger(AutoxChromeDriver.class);
	
	public AutoxChromeDriver(SeleniumDriverConfig config)
	{
		super(buildOptions(config));
	}
	
	private static ChromeOptions buildOptions(SeleniumDriverConfig config)
	{
		ChromeOptions options = new ChromeOptions();
		
		logger.debug("Creating chrome options with profile-options: {}", config.getProfileOptions());
		
		options.setHeadless( "true".equals(config.getProfileOptions().get("headless.execution")) );
		
		if("true".equals( config.getProfileOptions().get("enable.console.logs") ))
		{
			LoggingPreferences logPrefs = new LoggingPreferences();
	        logPrefs.enable(LogType.BROWSER, Level.ALL);
	        options.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
		}
		
		return options;
	}
}
