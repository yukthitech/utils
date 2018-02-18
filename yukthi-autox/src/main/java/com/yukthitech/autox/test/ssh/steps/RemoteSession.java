package com.yukthitech.autox.test.ssh.steps;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import com.yukthitech.ccg.xml.util.ValidateException;

/**
 * The Class RemoteSession.
 */
public class RemoteSession
{
	private static Logger logger = LogManager.getLogger(RemoteSession.class);
	
	/**
	 * JSH remote user info object used by jsh.
	 * @author akiran
	 */
	private class RemoteUserInfo implements UserInfo
	{
		
		/* (non-Javadoc)
		 * @see com.jcraft.jsch.UserInfo#getPassphrase()
		 */
		@Override
		public String getPassphrase()
		{
			return null;
		}

		/* (non-Javadoc)
		 * @see com.jcraft.jsch.UserInfo#getPassword()
		 */
		@Override
		public String getPassword()
		{
			return password;
		}

		/* (non-Javadoc)
		 * @see com.jcraft.jsch.UserInfo#promptPassphrase(java.lang.String)
		 */
		@Override
		public boolean promptPassphrase(String arg0)
		{
			return true;
		}

		/* (non-Javadoc)
		 * @see com.jcraft.jsch.UserInfo#promptPassword(java.lang.String)
		 */
		@Override
		public boolean promptPassword(String arg0)
		{
			return true;
		}

		/* (non-Javadoc)
		 * @see com.jcraft.jsch.UserInfo#promptYesNo(java.lang.String)
		 */
		@Override
		public boolean promptYesNo(String arg0)
		{
			return true;
		}

		/* (non-Javadoc)
		 * @see com.jcraft.jsch.UserInfo#showMessage(java.lang.String)
		 */
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

	/**
	 * Gets the host on which remote file is located.
	 *
	 * @return the host on which remote file is located
	 */
	public String getHost()
	{
		return host;
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
	 * Gets the sSH port, defaults to 22.
	 *
	 * @return the sSH port, defaults to 22
	 */
	public int getPort()
	{
		return port;
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
	 * Gets the user name to be used.
	 *
	 * @return the user name to be used
	 */
	public String getUser()
	{
		return user;
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
	 * Gets the password to be used. Either of password or privateKeyPath is mandatory.
	 *
	 * @return the password to be used
	 */
	public String getPassword()
	{
		return password;
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
	 * Gets the private key path. Either of password or privateKeyPath is mandatory.
	 *
	 * @return the private key path
	 */
	public String getPrivateKeyPath()
	{
		return privateKeyPath;
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
	public Session getSession() throws JSchException
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
	public ChannelSftp getChannelSftp() throws JSchException
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
	
	public void close()
	{
		if(sftp != null)
		{
			sftp.disconnect();
			sftp = null;
		}
		
		if(session != null)
		{
			session.disconnect();
			session = null;
		}
	}

	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(host))
		{
			throw new ValidateException("Host can not be null or empty");
		}
		
		if(StringUtils.isBlank(user))
		{
			throw new ValidateException("User can not be null or empty");
		}
		
		if(StringUtils.isBlank(password) && StringUtils.isBlank(privateKeyPath))
		{
			throw new ValidateException("Either of password or privateKeyPath is mandatory");
		}
	}
}
