package com.yukthitech.autox;

import java.io.File;
import java.util.Date;

import com.yukthitech.autox.test.TestStatus;
import com.yukthitech.autox.test.log.LogLevel;

public interface IExecutionLogger
{

	/**
	 * Sets the mode to be prepended for every log message.
	 *
	 * @param mode the new mode to be prepended for every log message
	 */
	void setMode(String mode);

	/**
	 * Clears the mode from the logger.
	 */
	void clearMode();

	/**
	 * Sets the flag indicating if logging is disabled or not.
	 *
	 * @param disabled the new flag indicating if logging is disabled or not
	 */
	void setDisabled(boolean disabled);

	/**
	 * Gets the flag indicating if logging is disabled or not.
	 *
	 * @return the flag indicating if logging is disabled or not
	 */
	boolean isDisabled();

	/**
	 * Used to log error messages as part of current execution.
	 * @param escapeHtml Whether html tags should be escaped in result content.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	void error(boolean escapeHtml, String mssgTemplate, Object... args);

	/**
	 * Used to log error messages as part of current execution.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	void error(String mssgTemplate, Object... args);

	/**
	 * Used to log debug messages as part of current execution.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	void debug(String mssgTemplate, Object... args);

	/**
	 * Used to log debug messages as part of current execution.
	 * @param escapeHtml Whether html tags should be escaped in result content.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	void debug(boolean escapeHtml, String mssgTemplate, Object... args);

	/**
	 * Used to log info messages as part of current execution.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	void info(String mssgTemplate, Object... args);

	/**
	 * Used to log info messages as part of current execution.
	 * @param escapeHtml Whether html tags should be escaped in result content.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	void info(boolean escapeHtml, String mssgTemplate, Object... args);

	/**
	 * Used to log warn messages as part of current execution.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	void warn(String mssgTemplate, Object... args);

	/**
	 * Used to log warn messages as part of current execution.
	 * @param escapeHtml Whether html tags should be escaped in result content.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	void warn(boolean escapeHtml, String mssgTemplate, Object... args);

	/**
	 * Used to log trace messages as part of current execution.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	void trace(String mssgTemplate, Object... args);

	/**
	 * Used to log trace messages as part of current execution.
	 * @param escapeHtml Whether html tags should be escaped in result content.
	 * @param source location from where logging is being done
	 * @param mssgTemplate Message template with params.
	 * @param args Arguments for message template.
	 */
	void trace(boolean escapeHtml, String mssgTemplate, Object... args);

	/**
	 * Logs the message at specified level.
	 * @param source location from where logging is being done
	 * @param logLevel level of log
	 * @param mssgTemplate msg template
	 * @param args arguments for message
	 */
	void log(LogLevel logLevel, String mssgTemplate, Object... args);

	/**
	 * Logs the message at specified level.
	 * @param escapeHtml Whether html tags should be escaped in result content.
	 * @param source location from where logging is being done
	 * @param logLevel level of log
	 * @param mssgTemplate msg template
	 * @param args arguments for message
	 */
	void log(boolean escapeHtml, LogLevel logLevel, String mssgTemplate, Object... args);

	/**
	 * Adds the specified image file to the debug log.
	 * @param source location from where logging is being done
	 * @param name Name of the image file for easy identification
	 * @param message Message to be logged along with image
	 * @param imageFile Image to be logged
	 * @param logLevel level to be used.
	 */
	void logImage(String name, String message, File imageFile, LogLevel logLevel);

	File createFile(String filePrefix, String fileSuffix);

	void logFile(String message, LogLevel logLevel, File file);

	File logFile(String message, LogLevel logLevel, String filePrefix, String fileSuffix);

	void close(TestStatus status, Date endTime);

}