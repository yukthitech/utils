package com.yukthitech.automation.logmon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Monitors system output and error streams and when required redirects the
 * streams to a log file. During monitoring data will be written to standard streams and also
 * to log file.
 * 
 * @author akiran
 */
public class ConsoleLogMonitor extends AbstractLogMonitor
{
	private static Logger logger = LogManager.getLogger(ConsoleLogMonitor.class);
	
	/**
	 * Proxy writer which works as proxy to out/err stream and when available
	 * writes to log file also.
	 * 
	 * @author akiran
	 */
	private class ProxyWriter extends OutputStream
	{
		/**
		 * Actual output stream which is getting proxied.
		 */
		private OutputStream mainStream;

		public ProxyWriter(OutputStream mainStream)
		{
			this.mainStream = mainStream;
		}

		@Override
		public void write(int b) throws IOException
		{
			mainStream.write(b);

			logLock.lock();

			try
			{
				if(logStream != null)
				{
					logStream.write(b);
				}
			} finally
			{
				logLock.unlock();
			}
		}

		@Override
		public void write(byte[] b) throws IOException
		{
			mainStream.write(b);

			logLock.lock();

			try
			{
				if(logStream != null)
				{
					logStream.write(b);
				}
			} finally
			{
				logLock.unlock();
			}
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException
		{
			mainStream.write(b, off, len);

			logLock.lock();

			try
			{
				if(logStream != null)
				{
					logStream.write(b, off, len);
				}
			} finally
			{
				logLock.unlock();
			}
		}
	}

	/**
	 * Proxy for sysout.
	 */
	private ProxyWriter sysOutWriter = new ProxyWriter(System.out);

	/**
	 * Proxy for syserr.
	 */
	private ProxyWriter sysErrWriter = new ProxyWriter(System.err);

	/**
	 * Lock to synchronize log file access.
	 */
	private ReentrantLock logLock = new ReentrantLock();

	/**
	 * Log file created when required.
	 */
	private File logFile;

	/**
	 * Log file stream to which redirection will be done.
	 */
	private FileOutputStream logStream;

	public ConsoleLogMonitor()
	{
		System.setOut(new PrintStream(sysOutWriter));
		System.setErr(new PrintStream(sysErrWriter));
	}

	@Override
	public void startMonitoring()
	{
		try
		{
			//create new temp log file
			logFile = File.createTempFile("temp", ".log");
			logLock.lock();

			//set the stream so that redirection will start
			try
			{
				logStream = new FileOutputStream(logFile);
			} finally
			{
				logLock.unlock();
			}
		} catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while creating temp log file");
		}
	}

	@Override
	public File stopMonitoring()
	{
		//if no redirection is active, simply ignore
		if(logStream == null)
		{
			throw new InvalidStateException("Log monitor is not started yet");
		}
		
		//use temp variables so that main variables can be freed quickly
		FileOutputStream fos = logStream;
		File logFile = this.logFile;

		//lock and make stream null so that further redirection will not be done
		logLock.lock();
		
		try
		{
			logStream = null;
		} finally
		{
			logLock.unlock();
		}
		
		//close redirection stream
		try
		{
			fos.close();
		}catch(Exception ex)
		{
			logger.error("An error occurred while closing log file: " + logFile.getPath(), ex);
		}
		
		this.logFile = null;
		return logFile;
	}
}
