package com.yukthitech.autox.logmon;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
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

	/* (non-Javadoc)
	 * @see com.yukthitech.automation.logmon.ILogMonitor#startMonitoring()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void startMonitoring(AutomationContext context)
	{
		String paths[] = this.remoteFilePaths.split("\\s*\\,\\s*");
		
		for(String remotePath : paths)
		{
			try
			{
				Matcher matcher = REMOTE_FILE_PATTERN.matcher(remotePath);
				
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
				throw new InvalidStateException(ex, "An error occurred while getting remote file size. Remote file - {}", remotePath);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.autox.logmon.ILogMonitor#stopMonitoring()
	 */
	@Override
	public List<LogFile> stopMonitoring(AutomationContext context)
	{
		List<LogFile> logFiles = new ArrayList<>(pathToSession.size());
		
		for(String remotePath : pathToSession.keySet())
		{
			RemoteSessionWithPosition remoteSession = pathToSession.get(remotePath);
			LogFile logFile = stopMonitoringSession(remotePath, remoteSession);
			
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
	private LogFile stopMonitoringSession(String remoteFilePath, RemoteSessionWithPosition remoteSession)
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
			
			File tempFile = null;
			
			try
			{
				tempFile = File.createTempFile("file-monitoring", ".log");
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while creating temp file", ex);
			}

			ChannelSftp.LsEntry lsEntry = lsEntries.get(0);
			long currentSize = lsEntry.getAttrs().getSize();
			
			//if there is no content simply return empty file.
			if(currentSize == 0)
			{
				return new LogFile(remoteFilePath + "@" + remoteSession.getHost(), tempFile);
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
				
				FileOutputStream fos = new FileOutputStream(tempFile);
				
				sftp.get(remoteFilePath, fos, progressMonitor, ChannelSftp.RESUME, remoteSession.position);
				
				fos.close();
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while creating monitoring log.", ex);
			}
			
			return new LogFile(remoteFilePath + "@" + remoteSession.getHost(), tempFile);
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while getting remote file size. Remote file - {}", remoteFilePath);
		}
		
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.util.Validateable#validate()
	 */
	@Override
	public void validate() throws ValidateException
	{
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
	}
}
