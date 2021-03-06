package com.yukthitech.ccg.xml;

import org.xml.sax.Locator;

/**
 * <BR>
 * <BR>
 * Thrown when loading od XML data fails. <BR>
 * 
 * @author A. Kranthi Kiran
 */
public class XMLLoadException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	/**
	 * Build XMLLoadException with specified values.
	 * 
	 * @param mssg
	 *            Message
	 * @param thr
	 *            Root cause.
	 * @param node
	 *            Node responsible for exception.
	 */
	public XMLLoadException(String mssg, Throwable thr, BeanNode node, Locator locator)
	{
		super(buildMessage(mssg, node, locator), thr);
	}

	/**
	 * Build XMLLoadException with specified values.
	 * 
	 * @param mssg
	 *            Message
	 * @param node
	 *            Node responsible for exception.
	 */
	public XMLLoadException(String mssg, BeanNode node, Locator locator)
	{
		this(mssg, null, node, locator);
	}

	/**
	 * Build XMLLoadException with specified values.
	 * 
	 * @param mssg
	 *            Message
	 * @param thr
	 *            Root cause.
	 */
	public XMLLoadException(String mssg, Throwable thr, Locator locator)
	{
		this(mssg, thr, null, locator);
	}

	/**
	 * Builds the body of this exception.
	 * 
	 * @param mssg
	 *            Message
	 * @param thr
	 *            Root cause.
	 * @param path
	 *            Path represnting XML node repsonsible for this exception.
	 * @return String represnting the body of this exception.
	 */
	private static String buildMessage(String mssg, BeanNode node, Locator locator)
	{
		StringBuffer buff = new StringBuffer();
		
		if(locator != null)
		{
			buff.append("[Line: ").append(locator.getLineNumber()).append(", Column: ").append(locator.getColumnNumber()).append("] ");
		}

		buff.append(mssg);

		if(node != null)
		{
			buff.append("\nPath: " + node.getNodePath());
		}

		return buff.toString();
	}
}
