package com.yukthitech.ccg.xml.writer;

import com.yukthitech.utils.exceptions.UtilsException;

/**
 * Thrown when there was an error in xml bean writing.
 * @author akiran
 */
public class XmlWriteException extends UtilsException
{
	private static final long serialVersionUID = 1L;

	public XmlWriteException(String message, Object... args)
	{
		super(message, args);
	}

	public XmlWriteException(Throwable cause, String message, Object... args)
	{
		super(cause, message, args);
	}
}
