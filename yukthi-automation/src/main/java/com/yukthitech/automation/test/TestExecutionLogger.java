package com.yukthitech.automation.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.automation.IExecutionLogger;
import com.yukthitech.utils.MessageFormatter;

/**
 * A simple internal logger to consolidate execution messages in test result.
 * 
 * @author akiran
 */
class TestExecutionLogger implements IExecutionLogger
{
	private static Logger logger = LogManager.getLogger(TestExecutionLogger.class);

	/**
	 * The new line.
	 **/
	private static String newLine = "\n";

	/**
	 * The error.
	 **/
	private static String error = "ERROR: ";

	/**
	 * Internal writer which takes care of indentations.
	 * 
	 * @author akiran
	 */
	private class LogWriter extends Writer
	{
		/**
		 * Main writer.
		 */
		private StringWriter writer = new StringWriter();

		/**
		 * Indentation string to be used.
		 */
		private String indentStr;

		/**
		 * Builds the indentStr according to specified indent.
		 * 
		 * @param indent
		 *            Indentation to be used.
		 */
		public void setIndent(int indent)
		{
			if(indent <= 0)
			{
				indentStr = null;
				return;
			}

			char tabChars[] = new char[indent];
			Arrays.fill(tabChars, '\t');

			this.indentStr = new String(tabChars);
		}

		@Override
		public void write(char[] cbuf, int off, int len) throws IOException
		{
			if(indentStr == null)
			{
				writer.write(cbuf, off, len);
				return;
			}

			String str = new String(cbuf, off, len);
			str = str.replace(newLine, indentStr + newLine);

			writer.write(str);
		}

		@Override
		public void flush()
		{
			writer.flush();
		}

		@Override
		public void close() throws IOException
		{}

		@Override
		public String toString()
		{
			return writer.getBuffer().toString();
		}
	}

	/**
	 * Sublogger of the main logger.
	 * 
	 * @author akiran
	 */
	private class Sublogger implements IExecutionLogger
	{
		/**
		 * Indentation to be used for messages of this logger.
		 */
		private int indentation;

		/**
		 * Instantiates a new sublogger.
		 *
		 * @param indentation
		 *            the indentation
		 */
		public Sublogger(int indentation)
		{
			this.indentation = indentation;
		}

		@Override
		public IExecutionLogger getSubLogger()
		{
			return new Sublogger(indentation + 1);
		}

		@Override
		public void error(String mssgTemplate, Object... args)
		{
			logWriter.setIndent(indentation);
			TestExecutionLogger.this.error(mssgTemplate, args);
			logWriter.setIndent(0);
		}

		@Override
		public void error(Throwable th, String mssgTemplate, Object... args)
		{
			logWriter.setIndent(indentation);
			TestExecutionLogger.this.error(th, mssgTemplate, args);
			logWriter.setIndent(0);
		}

		@Override
		public void debug(String mssgTemplate, Object... args)
		{
			logWriter.setIndent(indentation);
			TestExecutionLogger.this.debug(mssgTemplate, args);
			logWriter.setIndent(0);
		}
	}

	/**
	 * Writer to be used.
	 */
	private LogWriter logWriter = new LogWriter();

	/**
	 * Print writer.
	 */
	private PrintWriter printWriter;

	/**
	 * Instantiates a new execution logger.
	 */
	public TestExecutionLogger()
	{
		this.printWriter = new PrintWriter(logWriter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.ui.automation.IExecutionLogger#error(java.lang.String,
	 * java.lang.Object[])
	 */
	@Override
	public void error(String mssgTemplate, Object... args)
	{
		String finalMssg = MessageFormatter.format(mssgTemplate, args);
		logger.error(finalMssg);
		printWriter.println(error + finalMssg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.ui.automation.IExecutionLogger#error(java.lang.Throwable,
	 * java.lang.String, java.lang.Object[])
	 */
	@Override
	public void error(Throwable th, String mssgTemplate, Object... args)
	{
		String finalMssg = MessageFormatter.format(mssgTemplate, args);
		logger.error(finalMssg, th);

		th.printStackTrace(printWriter);
		printWriter.println(error + finalMssg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.ui.automation.IExecutionLogger#debug(java.lang.String,
	 * java.lang.Object[])
	 */
	@Override
	public void debug(String mssgTemplate, Object... args)
	{
		String finalMssg = MessageFormatter.format(mssgTemplate, args);

		logger.debug(finalMssg);
		printWriter.println("DEBUG: " + finalMssg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.automation.IExecutionLogger#getSubLogger()
	 */
	@Override
	public IExecutionLogger getSubLogger()
	{
		return new Sublogger(1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		printWriter.flush();
		logWriter.flush();

		return logWriter.toString();
	}
}
