package com.yukthitech.autox.test.log;

/**
 * Logging levels.
 * @author akiran
 */
public enum LogLevel
{
	TRACE("  TRACE"), 
	DEBUG("  DEBUG"), 
	WARN("   WARN"),
	ERROR("  ERROR"),
	INFO("  INFO"),
	
	/**
	 * Log level which would add messages to summary report.
	 */
	SUMMARY("SUMMARY");
	
	/**
	 * Padding string for this level.
	 */
	private String paddedString;

	private LogLevel(String paddedString)
	{
		this.paddedString = paddedString;
	}
	
	/**
	 * Gets the padding string for this level.
	 *
	 * @return the padding string for this level
	 */
	public String getPaddedString()
	{
		return paddedString;
	}
}
