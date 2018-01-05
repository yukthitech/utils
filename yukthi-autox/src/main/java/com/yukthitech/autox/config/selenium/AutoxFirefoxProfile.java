package com.yukthitech.autox.config.selenium;

import org.openqa.selenium.firefox.FirefoxProfile;

/**
 * Wrapper of firefox profile.
 * @author akiran
 *
 */
public class AutoxFirefoxProfile extends FirefoxProfile
{
	public void setStringPreference(String key, String value)
	{
		super.setPreference(key, value);
	}

	public void setBooleanPreference(String key, boolean value)
	{
		super.setPreference(key, value);
	}

	public void setIntPreference(String key, int value)
	{
		super.setPreference(key, value);
	}
}
