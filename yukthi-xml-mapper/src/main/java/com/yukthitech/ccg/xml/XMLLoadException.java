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
	
	private Integer lineNumber;
	
	private Integer column;
	
	private String nodePath;

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
		
		if(locator != null)
		{
			this.lineNumber = locator.getLineNumber();
			this.column = locator.getColumnNumber();
		}
		
		if(node != null)
		{
			this.nodePath = node.getNodePath();
		}
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
	
	public int getLineNumber()
	{
		return lineNumber;
	}

	public int getColumn()
	{
		return column;
	}

	public String getNodePath()
	{
		return nodePath;
	}
	
	public boolean hasLocation()
	{
		return (lineNumber != null);
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
