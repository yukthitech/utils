package com.yukthitech.persistence;

import java.util.Properties;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Exception to be thrown when configuration error is encountered.
 * @author akiran
 */
public class RepositoryConfigurationException extends PersistenceException
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Code to message mapping loaded from class path resource.
	 */
	private static Properties properties = new Properties();
	
	/**
	 * Static block to load configuration messages based on code.
	 */
	static
	{
		try
		{
			properties.load(RepositoryConfigurationException.class.getResourceAsStream("/configuration-exception-messages.properties"));
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occirred while loading resource file: /configuration-exception-messages.properties");
		}
	}
	
	/**
	 * Configuration error code.
	 */
	private String code;
	
	/**
	 * Fetches message based on specified error code.
	 * @param code Error code for which message needs to be fetched.
	 * @return Mapping message
	 */
	private static String getMessage(String code)
	{
		String message = properties.getProperty(code);
		
		if(message == null)
		{
			message = code;
		}
		
		return message;
	}

	/**
	 * Instantiates a new configuration error exception.
	 *
	 * @param code the code
	 * @param args the args
	 */
	public RepositoryConfigurationException(String code, Object... args)
	{
		super(getMessage(code), args);
		this.code = code;
	}

	/**
	 * Instantiates a new configuration error exception.
	 *
	 * @param cause the cause
	 * @param code the code
	 * @param args the args
	 */
	public RepositoryConfigurationException(Throwable cause, String code, Object... args)
	{
		super(cause, getMessage(code), args);
	}
	
	/**
	 * Gets the configuration error code.
	 *
	 * @return the configuration error code
	 */
	public String getCode()
	{
		return code;
	}
	
}
