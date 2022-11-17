package com.yukthitech.autox.logmon;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpProgressMonitor;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ReportLogFile;
import com.yukthitech.autox.test.ssh.steps.RemoteSession;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Remote file monitor to monitor the changes in remote ssh file.
 * @author akiran
 */
public class RemoteFileLogMonitor extends AbstractLogMonitor implements Validateable
{
	private static Logger logger = LogManager.getLogger(RemoteFileLogMonitor.class);
	
	private static final Pattern REMOTE_FILE_PATTERN = Pattern.compile("(?<path>.*)\\@(?<host>.*)\\:(?<port>\\d+)");
	
	private static class RemoteSessionWithPosition extends RemoteSession
	{
		private long position;
		
		public RemoteSessionWithPosition(String host, int port, String user, String password, String privateKeyPath)
		{
			super(host, port, user, password, privateKeyPath);
		}
	}
	
	/**
	 * List of remote file paths in format
	 * 		filePath@host:port, filePath@host:port
	 */
	private String remoteFilePaths;
	
	/**
	 * User to be used for authentication.
	 */
	private String user;
	
	/**
	 * Password for authentication.
	 */
	private String password;
	
	/**
	 * Private key path to be used instead of password.
	 */
	private String privateKeyPath;
	
	/**
	 * Delay time in seconds after which the log content will be fetched. This
	 * will help to make sure buffered log content is pushed by remote applications
	 * into log file. 
	 */
	private long fetchDelayInSec = 5;
	
	/**
	 * Number of times this logger failed to fetch the log.
	 */
	private int failureCount = 0;
	
	/**
	 * Maximum number of failures after this logger will self disable. Defaults to 4.
	 */
	private int maxFailureCount = 4;
	
	/**
	 * Host to session mapping.8o
	 */
	private Map<String, RemoteSessionWithPosition> pathToSession = new HashMap<>();
	
	public RemoteFileLogMonitor()
	{
	}
	
	/**
	 * Adds remote file path.
	 * @param remoteFilePaths path to be added.
	 */
	public void addRemoteFilePaths(String remoteFilePaths)
	{
		this.remoteFilePaths = remoteFilePaths;
	}

	/**
	 * Sets the user name to be used.
	 *
	 * @param user the new user name to be used
	 */
	public void setUser(String user)
	{
		this.user = user;
	}

	/**
	 * Sets the password to be used. Either of password or privateKeyPath is mandatory.
	 *
	 * @param password the new password to be used
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * Sets the private key path. Either of password or privateKeyPath is mandatory.
	 *
	 * @param privateKeyPath the new private key path
	 */
	public void setPrivateKeyPath(String privateKeyPath)
	{
		this.privateKeyPath = privateKeyPath;
	}
	
	/**
	 * Sets the delay time in seconds after which the log content will be
	 * fetched. This will help to make sure buffered log content is pushed by
	 * remote applications into log file.
	 *
	 * @param fetchDelayInSec
	 *            the new delay time in seconds after which the log content will
	 *            be fetched
	 */
	public void setFetchDelayInSec(long fetchDelayInSec)
	{
		this.fetchDelayInSec = fetchDelayInSec;
	}
	
	/**
	 * Sets the maximum number of failures after this logger will self disable.
	 *
	 * @param maxFailureCount
	 *            the new maximum number of failures after this logger will self
	 *            disable
	 */
	public void setMaxFailureCount(int maxFailureCount)
	{
		this.maxFailureCount = maxFailureCount;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.automation.logmon.ILogMonitor#startMonitoring()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void startMonitoring(AutomationContext context)
	{
		if(!super.isEnabled())
		{
			logger.warn("As this log monitor is not enabled, skipping start-monitor call");
			return;
		}
		
		if(failureCount >= maxFailureCount)
		{
			logger.warn("As this log monitor is failed more than max-failure-count, skipping start-monitor call");
			return;
		}

		String paths[] = this.remoteFilePaths.split("\\s*\\,\\s*");
		
		for(String remotePath : paths)
		{
			try
			{
				Matcher matcher = REMOTE_FILE_PATTERN.matcher(remotePath);
				
				if(!matcher.matches())
				{
					logger.error("Invalid remote file path specified. It should be of pattern '/remoteFilePath@host:port'. Ignoring errored file path: {}", remotePath);
					continue;
				}
				
				RemoteSessionWithPosition remoteSession = new RemoteSessionWithPosition(matcher.group("host"), Integer.parseInt(matcher.group("port")), user, password, privateKeyPath);
				String remoteFilePath = matcher.group("path");
				ChannelSftp sftp = remoteSession.getChannelSftp();
				
				logger.debug("Getting position of remote file: {}", remoteFilePath);
				
				Vector<ChannelSftp.LsEntry> lsEntries = sftp.ls(remoteFilePath);
				
				pathToSession.put(remoteFilePath, remoteSession);
				
				if(lsEntries.size() == 0)
				{
					logger.warn("No remote file found under specified path at start of monitoring. Assuming the file will be created. Path: " + remoteFilePath);
					return;
				}
				
				ChannelSftp.LsEntry lsEntry = lsEntries.get(0);
				remoteSession.position = lsEntry.getAttrs().getSize();
			}catch(Exception ex)
			{
				logger.error("An error occurred while getting remote file size. Remote file - {} [User: {}, Using Password: {}]", remotePath, user, (password != null), ex);
				failureCount++;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.autox.logmon.ILogMonitor#stopMonitoring()
	 */
	@Override
	public List<ReportLogFile> stopMonitoring(AutomationContext context)
	{
		if(!super.isEnabled())
		{
			logger.warn("As this log monitor is not enabled, skipping stop-monitor call");
			return Collections.emptyList();
		}
		
		if(failureCount >= maxFailureCount)
		{
			logger.warn("As this log monitor is failed more than max-failure-count, skipping stop-monitor call");
			return Collections.emptyList();
		}

		if(fetchDelayInSec > 0)
		{
			logger.debug("Waiting for {} Sec before fetching the logs.", fetchDelayInSec);
			
			try
			{
				Thread.sleep(fetchDelayInSec * 1000);
			} catch(Exception ex)
			{
				//ignore
			}
		}
		
		List<ReportLogFile> logFiles = new ArrayList<>(pathToSession.size());
		
		for(String remotePath : pathToSession.keySet())
		{
			RemoteSessionWithPosition remoteSession = pathToSession.get(remotePath);
			ReportLogFile logFile = stopMonitoringSession(remotePath, remoteSession);
			
			if(logFile != null)
			{
				logFiles.add(logFile);
			}
		}
		
		return logFiles;
	}

	/**
	 * Stops monitoring remote session and fetches corresponding log file.
	 * @param remoteSession remote session to stop monitoring
	 * @return corresponding log file
	 */
	@SuppressWarnings("unchecked")
	private ReportLogFile stopMonitoringSession(String remoteFilePath, RemoteSessionWithPosition remoteSession)
	{
		try
		{
			ChannelSftp sftp = remoteSession.getChannelSftp();
			
			logger.debug("Getting content from remote file: {}", remoteFilePath);
			
			Vector<ChannelSftp.LsEntry> lsEntries = sftp.ls(remoteFilePath);
			
			if(lsEntries.size() == 0)
			{
				logger.warn("No file found under specified remote path at end of monitoring. Ignoring monitoring request. Path: " + remoteFilePath);
				return null;
			}
			
			ReportLogFile tempFile = AutomationContext.getInstance().newLogFile(super.getName(), ".log");
			
			ChannelSftp.LsEntry lsEntry = lsEntries.get(0);
			long currentSize = lsEntry.getAttrs().getSize();
			
			//if there is no content simply return empty file.
			if(currentSize == 0)
			{
				return tempFile;
			}

			//if current size is less than start size
			//	which can happen in rolling logs, read the current file from starting
			if(currentSize < remoteSession.position)
			{
				remoteSession.position = 0;
			}

			//calculate amount of log to be read
			final long dataToRead = currentSize - remoteSession.position;
			
			try
			{
				//monitor which forces to stop getting data once 
				//	required bytes are read
				SftpProgressMonitor progressMonitor = new SftpProgressMonitor()
				{
					@Override
					public void init(int arg0, String arg1, String arg2, long arg3)
					{
					}
					
					@Override
					public void end()
					{
					}
					
					@Override
					public boolean count(long read)
					{
						return read >= dataToRead;
					}
				};
				
				FileOutputStream fos = new FileOutputStream(tempFile.getFile());
				
				sftp.get(remoteFilePath, fos, progressMonitor, ChannelSftp.RESUME, remoteSession.position);
				
				fos.close();
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while creating monitoring log.", ex);
			}
			
			return tempFile;
		}catch(Exception ex)
		{
			logger.error("An error occurred while getting remote file size. Remote file - {}", remoteFilePath, ex);
			failureCount++;
			return null;
		}
		
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.util.Validateable#validate()
	 */
	@Override
	public void validate() throws ValidateException
	{
		if(!isEnabled())
		{
			return;
		}
		
		super.validate();
		
		if(StringUtils.isEmpty(remoteFilePaths))
		{
			throw new ValidateException("No remote file path specified");
		}
		
		String paths[] = remoteFilePaths.split("\\s*\\,\\s*");
		
		for(String path : paths)
		{
			if(!REMOTE_FILE_PATTERN.matcher(path).matches())
			{
				throw new ValidateException("Invalid remote file path specified: " + path);
			}
		}
		
		if(StringUtils.isBlank(user))
		{
			throw new ValidateException("No user name is specified");
		}
		
		if(StringUtils.isBlank(password) && StringUtils.isBlank(privateKeyPath))
		{
			throw new ValidateException("Either of password or private-key-path is mandatory.");
		}
		
		if(StringUtils.isNotBlank(privateKeyPath))
		{
			File keyFile = new File(privateKeyPath);
			
			if(!keyFile.exists())
			{
				throw new ValidateException("Specified private key file does not exist: " + privateKeyPath);
			}
		}
	}
}
