package com.yukthitech.autox.exec.report;

import java.io.File;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.test.TestStatus;

public class Log4jExecutionLogger implements IExecutionLogger
{
	private static Logger logger = LogManager.getLogger(Log4jExecutionLogger.class);
	
	private static Log4jExecutionLogger instance = new Log4jExecutionLogger();
	
	public static Log4jExecutionLogger getInstance()
	{
		return instance;
	}
	
	@Override
	public void setMode(String mode)
	{
	}

	@Override
	public void clearMode()
	{
	}

	@Override
	public void setDisabled(boolean disabled)
	{
	}

	@Override
	public boolean isDisabled()
	{
		return false;
	}

	@Override
	public void error(boolean escapeHtml, String mssgTemplate, Object... args)
	{
		logger.error(mssgTemplate, args);
	}

	@Override
	public void error(String mssgTemplate, Object... args)
	{
		logger.error(mssgTemplate, args);
	}

	@Override
	public void debug(String mssgTemplate, Object... args)
	{
		logger.debug(mssgTemplate, args);
	}

	@Override
	public void debug(boolean escapeHtml, String mssgTemplate, Object... args)
	{
		logger.debug(mssgTemplate, args);
	}

	@Override
	public void info(String mssgTemplate, Object... args)
	{
		logger.info(mssgTemplate, args);
	}

	@Override
	public void info(boolean escapeHtml, String mssgTemplate, Object... args)
	{
		logger.info(mssgTemplate, args);
	}

	@Override
	public void warn(String mssgTemplate, Object... args)
	{
		logger.warn(mssgTemplate, args);
	}

	@Override
	public void warn(boolean escapeHtml, String mssgTemplate, Object... args)
	{
		logger.warn(mssgTemplate, args);
	}

	@Override
	public void trace(String mssgTemplate, Object... args)
	{
		logger.trace(mssgTemplate, args);
	}

	@Override
	public void trace(boolean escapeHtml, String mssgTemplate, Object... args)
	{
		logger.trace(mssgTemplate, args);
	}

	@Override
	public void log(LogLevel logLevel, String mssgTemplate, Object... args)
	{
		logger.debug(mssgTemplate, args);
	}

	@Override
	public void log(boolean escapeHtml, LogLevel logLevel, String mssgTemplate, Object... args)
	{
		logger.debug(mssgTemplate, args);
	}

	@Override
	public void logImage(String name, String message, File imageFile, LogLevel logLevel)
	{
		logger.debug(message);
	}

	@Override
	public File createFile(String filePrefix, String fileSuffix)
	{
		return null;
	}

	@Override
	public void logFile(String message, LogLevel logLevel, File file)
	{
		logger.debug(message);
	}

	@Override
	public File logFile(String message, LogLevel logLevel, String filePrefix, String fileSuffix)
	{
		logger.debug(message);
		return null;
	}

	@Override
	public void close(TestStatus status, Date endTime)
	{
	}
}
