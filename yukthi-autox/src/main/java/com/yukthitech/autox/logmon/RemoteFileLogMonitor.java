package com.yukthitech.autox.logmon;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpProgressMonitor;
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
	/** The logger. */
	private static Logger logger = LogManager.getLogger(RemoteFileLogMonitor.class);
	
	/**
	 * Path of file to monitor.
	 */
	private String remoteFilePath;
	
	/**
	 * Internal field to track start position at start of monitoring.
	 */
	private long startPosition = -1;
	
	private RemoteSession remoteSession = new RemoteSession();
	
	public RemoteFileLogMonitor()
	{
	}

	/**
	 * Sets the path of file to monitor.
	 *
	 * @param remoteFilePath the new path of file to monitor
	 */
	public void setRemoteFilePath(String remoteFilePath)
	{
		this.remoteFilePath = remoteFilePath;
	}

	/**
	 * Sets the host on which remote file is located.
	 *
	 * @param host the new host on which remote file is located
	 */
	public void setHost(String host)
	{
		remoteSession.setHost(host);;
	}

	/**
	 * Sets the sSH port, defaults to 22.
	 *
	 * @param port the new sSH port, defaults to 22
	 */
	public void setPort(int port)
	{
		remoteSession.setPort(port);
	}

	/**
	 * Sets the user name to be used.
	 *
	 * @param user the new user name to be used
	 */
	public void setUser(String user)
	{
		remoteSession.setUser(user);
	}

	/**
	 * Sets the password to be used. Either of password or privateKeyPath is mandatory.
	 *
	 * @param password the new password to be used
	 */
	public void setPassword(String password)
	{
		remoteSession.setPassword(password);
	}

	/**
	 * Sets the private key path. Either of password or privateKeyPath is mandatory.
	 *
	 * @param privateKeyPath the new private key path
	 */
	public void setPrivateKeyPath(String privateKeyPath)
	{
		remoteSession.setPrivateKeyPath(privateKeyPath);
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.automation.logmon.ILogMonitor#startMonitoring()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void startMonitoring()
	{
		try
		{
			ChannelSftp sftp = remoteSession.getChannelSftp();
			
			logger.debug("Getting position of remote file: {}", remoteFilePath);
			
			Vector<ChannelSftp.LsEntry> lsEntries = sftp.ls(remoteFilePath);
			
			if(lsEntries.size() == 0)
			{
				logger.warn("No remote file found under specified path at start of monitoring. Assuming the file will be created. Path: " + remoteFilePath);
				startPosition = 0;
				return;
			}
			
			ChannelSftp.LsEntry lsEntry = lsEntries.get(0);
			startPosition = lsEntry.getAttrs().getSize();
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while getting remote file size. Remote file - {}", remoteFilePath);
		}
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.automation.logmon.ILogMonitor#stopMonitoring()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public File stopMonitoring()
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
				return tempFile;
			}

			//if current size is less than start size
			//	which can happen in rolling logs, read the current file from starting
			if(currentSize < startPosition)
			{
				startPosition = 0;
			}

			//calculate amount of log to be read
			final long dataToRead = currentSize - startPosition;
			
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
				
				sftp.get(remoteFilePath, fos, progressMonitor, ChannelSftp.RESUME, startPosition);
				
				fos.close();
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while creating monitoring log.", ex);
			}
			
			return tempFile;
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
		remoteSession.validate();
		
		if(StringUtils.isBlank(remoteFilePath))
		{
			throw new ValidateException("Remote file path can not be null or empty");
		}
	}
}
