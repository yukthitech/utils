package com.yukthitech.automation.config;

import com.yukthitech.utils.cli.CliArgument;

/**
 * Command line arguments expected by selenium configuration.
 * @author akiran
 */
public class SeleniumConfigurationArgs
{
	/**
	 * Name of the web driver to be used.
	 */
	@CliArgument(name = "wd", longName = "webdriver", description = "Webdriver to be used by selenium based test cases", required = false)
	private String webDriver;

	/**
	 * Gets the name of the web driver to be used.
	 *
	 * @return the name of the web driver to be used
	 */
	public String getWebDriver()
	{
		return webDriver;
	}

	/**
	 * Sets the name of the web driver to be used.
	 *
	 * @param webDriver the new name of the web driver to be used
	 */
	public void setWebDriver(String webDriver)
	{
		this.webDriver = webDriver;
	}
}
