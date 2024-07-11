/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.mail.tracker;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.PostConstruct;

/**
 * Settings or information required for sending and reading the mails.
 * @author akiran
 */
public class EmailServerSettings
{
	/**
	 * Property name for setting smtp host.
	 */
	public static final String PROP_SMTP_HOST = "mail.smtp.host";
	
	/**
	 * Property for setting smtp port.
	 */
	public static final String PROP_SMTP_PORT = "mail.smtp.port";
	
	/**
	 * Property indicating if auth is enabled or not.
	 */
	public static final String PROP_USE_AUTH = "mail.smtp.auth";
	
	/**
	 * Property indicating if TTLS should be used.
	 */
	public static final String PROP_ENABLE_TTLS = "mail.smtp.starttls.enable";
	
	public static final String PROP_ENABLE_SSL = "mail.smtp.ssl.enable";

	/**
	* Smtp host.
	*/
	private String smtpHost;
	
	/**
	 * Smtp port.
	 */
	private Integer smtpPort;

	/**
	 * Flag to indicate whether authentication to be used.
	 */
	private boolean useAuthentication = false;
	
	/**
	 * User name for authentication.
	 */
	private String userName;
	
	/**
	 * Email id to be used while sending the mail. If not specified userName will be used.
	 */
	private String fromId;
	
	/**
	 * Password for authentication.
	 */
	private String password;

	/**
	 * Flag indicating if ttls should be enabled.
	 */
	private boolean enableTtls = false;
	
	/**
	 * Flag to enable ssl.
	 */
	private boolean enableSsl = false;
	
	/**
	 * Protocol to be used for reading mails.
	 */
	private MailReadProtocol readProtocol;
	
	/**
	 * Host address from where mail can be read or deleted.
	 */
	private String readHost;
	
	/**
	 * Port to be used for reading mails.
	 */
	private int readPort;
	
	/**
	 * Folder from which mails needs to be accessed.
	 */
	private String folderName = "INBOX";
	
	private Map<String, String> imapProperties = new HashMap<>();
	
	private Map<String, String> smtpProperties = new HashMap<>();
	
	/**
	 * Gets the smtp host.
	 *
	 * @return the smtp host
	 */
	public String getSmtpHost()
	{
		return smtpHost;
	}

	/**
	 * Sets the smtp host.
	 *
	 * @param smtpHost the new smtp host
	 */
	public void setSmtpHost(String smtpHost)
	{
		this.smtpHost = smtpHost;
	}

	/**
	 * Gets the smtp port.
	 *
	 * @return the smtp port
	 */
	public Integer getSmtpPort()
	{
		return smtpPort;
	}

	/**
	 * Sets the smtp port.
	 *
	 * @param smtpPort the new smtp port
	 */
	public void setSmtpPort(Integer smtpPort)
	{
		this.smtpPort = smtpPort;
	}

	/**
	 * Checks if is flag to indicate whether authentication to be used.
	 *
	 * @return the flag to indicate whether authentication to be used
	 */
	public boolean isUseAuthentication()
	{
		return useAuthentication;
	}

	/**
	 * Sets the flag to indicate whether authentication to be used.
	 *
	 * @param useAuthentication the new flag to indicate whether authentication to be used
	 */
	public void setUseAuthentication(boolean useAuthentication)
	{
		this.useAuthentication = useAuthentication;
	}

	/**
	 * Gets the user name for authentication.
	 *
	 * @return the user name for authentication
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * Sets the user name for authentication.
	 *
	 * @param userName the new user name for authentication
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	/**
	 * Gets the password for authentication.
	 *
	 * @return the password for authentication
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets the password for authentication.
	 *
	 * @param password the new password for authentication
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * Checks if is flag indicating if ttls should be enabled.
	 *
	 * @return the flag indicating if ttls should be enabled
	 */
	public boolean isEnableTtls()
	{
		return enableTtls;
	}

	/**
	 * Sets the flag indicating if ttls should be enabled.
	 *
	 * @param enableTtls the new flag indicating if ttls should be enabled
	 */
	public void setEnableTtls(boolean enableTtls)
	{
		this.enableTtls = enableTtls;
	}
	
	/**
	 * Gets the protocol to be used for reading mails.
	 *
	 * @return the protocol to be used for reading mails
	 */
	public MailReadProtocol getReadProtocol()
	{
		return readProtocol;
	}

	/**
	 * Sets the protocol to be used for reading mails.
	 *
	 * @param readProtocol the new protocol to be used for reading mails
	 */
	public void setReadProtocol(MailReadProtocol readProtocol)
	{
		this.readProtocol = readProtocol;
	}

	/**
	 * Gets the host address from where mail can be read or deleted.
	 *
	 * @return the host address from where mail can be read or deleted
	 */
	public String getReadHost()
	{
		return readHost;
	}

	/**
	 * Sets the host address from where mail can be read or deleted.
	 *
	 * @param readHost the new host address from where mail can be read or deleted
	 */
	public void setReadHost(String readHost)
	{
		this.readHost = readHost;
	}
	
	/**
	 * Gets the port to be used for reading mails.
	 *
	 * @return the port to be used for reading mails
	 */
	public int getReadPort()
	{
		return readPort;
	}

	/**
	 * Sets the port to be used for reading mails.
	 *
	 * @param readPort the new port to be used for reading mails
	 */
	public void setReadPort(int readPort)
	{
		this.readPort = readPort;
	}

	/**
	 * Gets the folder from which mails needs to be accessed.
	 *
	 * @return the folder from which mails needs to be accessed
	 */
	public String getFolderName()
	{
		return folderName;
	}

	/**
	 * Sets the folder from which mails needs to be accessed.
	 *
	 * @param folderName the new folder from which mails needs to be accessed
	 */
	public void setFolderName(String folderName)
	{
		this.folderName = folderName;
	}

	/**
	 * Gets the email id to be used while sending the mail. If not specified userName will be used.
	 *
	 * @return the email id to be used while sending the mail
	 */
	public String getFromId()
	{
		return fromId;
	}

	/**
	 * Sets the email id to be used while sending the mail. If not specified userName will be used.
	 *
	 * @param fromId the new email id to be used while sending the mail
	 */
	public void setFromId(String fromId)
	{
		this.fromId = fromId;
	}

	/**
	 * Checks if is flag to enable ssl.
	 *
	 * @return the flag to enable ssl
	 */
	public boolean isEnableSsl()
	{
		return enableSsl;
	}

	/**
	 * Sets the flag to enable ssl.
	 *
	 * @param enableSsl the new flag to enable ssl
	 */
	public void setEnableSsl(boolean enableSsl)
	{
		this.enableSsl = enableSsl;
	}
	
	public Map<String, String> getImapProperties()
	{
		return imapProperties;
	}

	public void setImapProperties(Map<String, String> imapProperties)
	{
		this.imapProperties = imapProperties;
	}

	public Map<String, String> getSmtpProperties()
	{
		return smtpProperties;
	}

	public void setSmtpProperties(Map<String, String> smtpProperties)
	{
		this.smtpProperties = smtpProperties;
	}

	/**
	* Validates required configuration params are provided.
	*/
	@PostConstruct
	private void validate()
	{
		if(StringUtils.isEmpty(smtpHost))
		{
			throw new IllegalStateException("No SMTP host is provided");
		}

		if(useAuthentication)
		{
			if(StringUtils.isEmpty(userName) || StringUtils.isEmpty(password))
			{
				throw new IllegalStateException("No username/password is provided");
			}
		}
	}

	/**
	* Converts this configuration into properties compatible with java-mail.
	*
	* @return Java mail compatible properties.
	*/
	public Properties toProperties(MailReadProtocol protocol)
	{
		Properties props = new Properties();
		
		if(protocol == MailReadProtocol.POP3S)
		{
			props.put("mail.store.protocol", "pop3s");
			props.put("mail.pop3s.host", smtpHost);
			props.put("mail.pop3s.port", "" + smtpPort);
			props.put("mail.pop3s.auth", "true");
			props.put("mail.pop3s.socketFactory.class", SSLSocketFactory.class.getName());
			props.put("mail.pop3s.ssl.trust", "*");
			
			return props;
		}
		
		props.put(PROP_USE_AUTH, "" + useAuthentication);
		props.put(PROP_ENABLE_TTLS, "" + enableTtls);
		props.put(PROP_ENABLE_SSL, "" + enableSsl);
		
		props.put(PROP_SMTP_HOST, smtpHost);
		
		if(smtpPort != null)
		{
			props.put(PROP_SMTP_PORT, "" + smtpPort);
		}
		
		if(protocol == MailReadProtocol.IMAPS && imapProperties != null)
		{
			props.putAll(imapProperties);
		}
		
		if(protocol == null && smtpProperties != null)
		{
			props.putAll(smtpProperties);
		}

		return props;
	}
}
