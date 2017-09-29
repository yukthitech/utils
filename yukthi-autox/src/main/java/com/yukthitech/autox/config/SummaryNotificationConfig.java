package com.yukthitech.autox.config;

import org.apache.commons.lang3.StringUtils;

/**
 * Defines the configuration required by notification configuration.
 * @author akiran
 */
public class SummaryNotificationConfig
{
	/**
	 * Smtp server host.
	 */
	private String smptpHost;
	
	/**
	 * Smtp server port.
	 */
	private int smptpPort = 587;
	
	/**
	 * Flag indicating ttls enabled or not.
	 */
	private boolean ttlsEnabled = true;
	
	/**
	 * Smtp user name to be used.
	 */
	private String userName;
	
	/**
	 * Smtp password to be used.
	 */
	private String password;
	
	/**
	 * Id from which notification should be marked as sent.
	 */
	private String fromAddress;
	
	/**
	 * Space separated address list to which notification should be sent.
	 */
	private String toAddressList;
	
	/**
	 * Template fore creating subject line.
	 */
	private String subjectTemplate;
	
	public SummaryNotificationConfig()
	{}
	
	public SummaryNotificationConfig(String smptpHost, String userName, String password, String toAddressList, String subjectTemplate)
	{
		this.smptpHost = smptpHost;
		this.userName = userName;
		this.password = password;
		this.toAddressList = toAddressList;
		this.subjectTemplate = subjectTemplate;
	}

	/**
	 * Gets the smtp server host.
	 *
	 * @return the smtp server host
	 */
	public String getSmptpHost()
	{
		return smptpHost;
	}

	/**
	 * Sets the smtp server host.
	 *
	 * @param smptpHost the new smtp server host
	 */
	public void setSmptpHost(String smptpHost)
	{
		this.smptpHost = smptpHost;
	}

	/**
	 * Gets the smtp server port.
	 *
	 * @return the smtp server port
	 */
	public int getSmptpPort()
	{
		return smptpPort;
	}

	/**
	 * Sets the smtp server port.
	 *
	 * @param smptpPort the new smtp server port
	 */
	public void setSmptpPort(int smptpPort)
	{
		this.smptpPort = smptpPort;
	}

	/**
	 * Gets the flag indicating ttls enabled or not.
	 *
	 * @return the flag indicating ttls enabled or not
	 */
	public boolean isTtlsEnabled()
	{
		return ttlsEnabled;
	}

	/**
	 * Sets the flag indicating ttls enabled or not.
	 *
	 * @param ttlsEnabled the new flag indicating ttls enabled or not
	 */
	public void setTtlsEnabled(boolean ttlsEnabled)
	{
		this.ttlsEnabled = ttlsEnabled;
	}

	/**
	 * Gets the smtp user name to be used.
	 *
	 * @return the smtp user name to be used
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * Sets the smtp user name to be used.
	 *
	 * @param userName the new smtp user name to be used
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	/**
	 * Gets the smtp password to be used.
	 *
	 * @return the smtp password to be used
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets the smtp password to be used.
	 *
	 * @param password the new smtp password to be used
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	/**
	 * Fetches flag if auth is enabled or not.
	 * @return
	 */
	public boolean isAuthEnabled()
	{
		return ( StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password) ); 
	}

	/**
	 * Gets the id from which notification should be marked as sent.
	 *
	 * @return the id from which notification should be marked as sent
	 */
	public String getFromAddress()
	{
		if(fromAddress == null)
		{
			return userName;
		}
		
		return fromAddress;
	}

	/**
	 * Sets the id from which notification should be marked as sent.
	 *
	 * @param fromAddress the new id from which notification should be marked as sent
	 */
	public void setFromAddress(String fromAddress)
	{
		this.fromAddress = fromAddress;
	}

	/**
	 * Gets the space separated address list to which notification should be sent.
	 *
	 * @return the space separated address list to which notification should be sent
	 */
	public String getToAddressList()
	{
		return toAddressList;
	}

	/**
	 * Sets the space separated address list to which notification should be sent.
	 *
	 * @param toAddressList the new space separated address list to which notification should be sent
	 */
	public void setToAddressList(String toAddressList)
	{
		this.toAddressList = toAddressList;
	}

	/**
	 * Gets the template fore creating subject line.
	 *
	 * @return the template fore creating subject line
	 */
	public String getSubjectTemplate()
	{
		return subjectTemplate;
	}

	/**
	 * Sets the template fore creating subject line.
	 *
	 * @param subjectTemplate the new template fore creating subject line
	 */
	public void setSubjectTemplate(String subjectTemplate)
	{
		this.subjectTemplate = subjectTemplate;
	}
}
