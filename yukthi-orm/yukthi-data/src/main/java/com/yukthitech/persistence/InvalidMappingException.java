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
package com.yukthitech.persistence;

/**
 * Thrown when invalid mapping is encountered.
 * @author akiran
 */
public class InvalidMappingException extends PersistenceException
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new invalid mapping exception.
	 *
	 * @param message the message
	 * @param args the args
	 */
	public InvalidMappingException(String message, Object... args)
	{
		super(message, args);
	}

	/**
	 * Instantiates a new invalid mapping exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public InvalidMappingException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Instantiates a new invalid mapping exception.
	 *
	 * @param message the message
	 */
	public InvalidMappingException(String message)
	{
		super(message);
	}

	/**
	 * Instantiates a new invalid mapping exception.
	 *
	 * @param cause the cause
	 * @param message the message
	 * @param args the args
	 */
	public InvalidMappingException(Throwable cause, String message, Object... args)
	{
		super(cause, message, args);
	}
}
