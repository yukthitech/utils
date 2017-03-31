package com.yukthitech.automation.logmon;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpProgressMonitor;
import com.jcraft.jsch.UserInfo;
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
	
	private class RemoteUserInfo implements UserInfo
	{
		@Override
		public String getPassphrase()
		{
			return null;
		}

		@Override
		public String getPassword()
		{
			return password;
		}

		@Override
		public boolean promptPassphrase(String arg0)
		{
			return true;
		}

		@Override
		public boolean promptPassword(String arg0)
		{
			return true;
		}

		@Override
		public boolean promptYesNo(String arg0)
		{
			return true;
		}

		@Override
		public void showMessage(String arg0)
		{
		}
		
	}
	
	static
	{
		JSch.setConfig("StrictHostKeyChecking", "no");
	}
	
	/**
	 * Path of file to monitor.
	 */
	private String remoteFilePath;
	
	/**
	 * Host on which remote file is located.
	 */
	private String host;
	
	/**
	 * SSH port, defaults to 22.
	 */
	private int port = 22;
	
	/**
	 * User name to be used.
	 */
	private String user;
	
	/**
	 * Password to be used. Either of password or privateKeyPath is mandatory.
	 */
	private String password;
	
	/**
	 * Private key path. Either of password or privateKeyPath is mandatory.
	 */
	private String privateKeyPath;
	
	/**
	 * Internal field to track start position at start of monitoring.
	 */
	private long startPosition = -1;
	
	/**
	 * Jsch interaction instance.
	 */
	private JSch jsch=new JSch();
	
	/**
	 * Internal variable, used to maintain sftp connection.
	 */
	private ChannelSftp sftp;
	
	/**
	 * Internal variable, used to maintain session.
	 */
	private Session session;
	
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
		this.host = host;
	}

	/**
	 * Sets the sSH port, defaults to 22.
	 *
	 * @param port the new sSH port, defaults to 22
	 */
	public void setPort(int port)
	{
		this.port = port;
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
	 * Creates new session.
	 * @return new session
	 */
	private Session getSession() throws JSchException
	{
		if(session != null && session.isConnected())
		{
			return session;
		}
		
		if(StringUtils.isNoneBlank(password))
		{
			logger.debug("Starting ssh session with remote host using password - {}:{}", host, port);
			
			session = jsch.getSession(user, host, port);
			session.setUserInfo(new RemoteUserInfo());
		}
		else
		{
			logger.debug("Starting ssh session with remote host using private key - {}:{}", host, port);
			
			jsch.addIdentity(privateKeyPath);
			session = jsch.getSession(user, host, port);
		}
		
		session.connect();
		return session;
	}
	
	/**
	 * Opens a sftp channel and returns the same. If connection was already open, this 
	 * method returns old connection.
	 * @return sftp channel
	 * @throws JSchException 
	 */
	private ChannelSftp getChannel() throws JSchException
	{
		if(sftp != null && sftp.isConnected())
		{
			return sftp;
		}
		
		Session session = getSession();
		
		logger.debug("Establishing sftp connection to remote host: {}", host);

		ChannelSftp sftp = (ChannelSftp ) session.openChannel("sftp");
		sftp.connect();
		
		this.sftp = sftp;
		return sftp;
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
			ChannelSftp sftp = getChannel();
			
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
			ChannelSftp sftp = getChannel();
			
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
		
		if(StringUtils.isBlank(host))
		{
			throw new ValidateException("Host can not be null or empty");
		}
		
		if(StringUtils.isBlank(user))
		{
			throw new ValidateException("User can not be null or empty");
		}
		
		if(StringUtils.isBlank(remoteFilePath))
		{
			throw new ValidateException("Remote file path can not be null or empty");
		}
		
		if(StringUtils.isBlank(password) && StringUtils.isBlank(privateKeyPath))
		{
			throw new ValidateException("Either of password or privateKeyPath is mandatory");
		}
	}

}
