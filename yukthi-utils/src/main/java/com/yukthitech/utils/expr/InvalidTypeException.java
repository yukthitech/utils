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
package com.yukthitech.utils.expr;

import com.yukthitech.utils.exceptions.UtilsException;

/**
 * Thrown when invalid parameter type is encountered in an expression.
 * @author akiran
 */
public class InvalidTypeException extends UtilsException
{
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new invalid type exception.
	 *
	 * @param message the message
	 * @param args the args
	 */
	public InvalidTypeException(String message, Object... args)
	{
		super(message, args);
	}

	/**
	 * Instantiates a new invalid type exception.
	 *
	 * @param cause the cause
	 * @param message the message
	 * @param args the args
	 */
	public InvalidTypeException(Throwable cause, String message, Object... args)
	{
		super(cause, message, args);
	}
}
