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
package com.yukthitech.utils.exceptions;

import com.yukthitech.utils.MessageFormatter;

/**
 * Base exception for utils runtime exception
 * @author akiran
 */
public class UtilsException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new utils exception.
	 *
	 * @param message the message
	 * @param args the args
	 */
	public UtilsException(String message, Object... args)
	{
		super(buildMessage(message, args), getRootCause(args));
	}
	
	/**
	 * Checks if the last argument is throwable, if it is the same will be returned.
	 * @param args arguments to check
	 * @return throwable if found
	 */
	public static Throwable getRootCause(Object... args)
	{
		if(args == null || args.length == 0)
		{
			return null;
		}
		
		if(args[args.length - 1] instanceof Throwable)
		{
			return (Throwable)args[args.length - 1];
		}
		
		return null;
	}
	
	/**
	 * Replaces the argument holders in message with argument values.
	 * @param message Message to be processed.
	 * @param args Values to be used
	 * @return Processed message
	 */
	public static String buildMessage(String message, Object... args)
	{
		String strArgs[] = null;
		
		if(args != null)
		{
			strArgs = new String[args.length];
			
			for(int i = 0; i < args.length; i++)
			{
				strArgs[i] = "" + args[i];
			}
		}
		
		return MessageFormatter.format(message, (Object[])strArgs);
	}
}
