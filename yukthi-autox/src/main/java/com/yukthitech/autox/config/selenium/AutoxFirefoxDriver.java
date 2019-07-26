package com.yukthitech.autox.config.selenium;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import com.yukthitech.autox.config.SeleniumDriverConfig;

public class AutoxFirefoxDriver extends FirefoxDriver
{
	private static Logger logger = LogManager.getLogger(AutoxFirefoxDriver.class);
	
	public AutoxFirefoxDriver(SeleniumDriverConfig config)
	{
		super(buildOptions(config));
	}
	
	private static FirefoxOptions buildOptions(SeleniumDriverConfig config)
	{
		FirefoxOptions options = new FirefoxOptions();
		
		if(!config.getProfileOptions().isEmpty())
		{
			FirefoxProfile profile = new FirefoxProfile();
			
			logger.debug("Creating firefox profile with profile options as: {}", config.getProfileOptions());
			
			for(Map.Entry<String, Object> opt : config.getProfileOptions().entrySet())
			{
				if(opt.getValue() instanceof Boolean)
				{
					profile.setPreference(opt.getKey(), (Boolean) opt.getValue());
				}
				else if(opt.getValue() instanceof Integer)
				{
					profile.setPreference(opt.getKey(), (Integer) opt.getValue());
				}
				else
				{
					profile.setPreference(opt.getKey(), (String) opt.getValue());
				}
			}
			
			options.setProfile(profile);
		}
		
		return options;
	}
}
