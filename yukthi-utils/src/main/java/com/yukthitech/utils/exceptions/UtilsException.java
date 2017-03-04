/*
 * The MIT License (MIT)
 * Copyright (c) 2016 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yukthitech.utils.exceptions;

import java.util.regex.Matcher;

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
	 * @param cause the cause
	 * @param message the message
	 * @param args the args
	 */
	public UtilsException(Throwable cause, String message, Object... args)
	{
		super(buildMessage(message, args), cause);
	}

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
	static Throwable getRootCause(Object... args)
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
	static String buildMessage(String message, Object... args)
	{
		String strArgs[] = null;
		
		if(args != null)
		{
			strArgs = new String[args.length];
			
			for(int i = 0; i < args.length; i++)
			{
				strArgs[i] = "" + args[i];
				strArgs[i] = Matcher.quoteReplacement(strArgs[i]);
			}
		}
		
		return MessageFormatter.format(message, (Object[])strArgs);
	}
}
