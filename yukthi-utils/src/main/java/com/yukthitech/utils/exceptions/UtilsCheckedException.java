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
 * Base exception for utils checked exception
 * @author akiran
 */
public class UtilsCheckedException extends Exception
{
	private static final long serialVersionUID = 1L;

	public UtilsCheckedException(Throwable cause, String message, Object... args)
	{
		super(buildMessage(message, args), cause);
	}

	public UtilsCheckedException(String message, Object... args)
	{
		super(buildMessage(message, args));
	}
	
	private static String buildMessage(String message, Object... args)
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
