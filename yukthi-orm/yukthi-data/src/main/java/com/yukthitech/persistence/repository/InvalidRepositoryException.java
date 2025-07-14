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
package com.yukthitech.persistence.repository;

import com.yukthitech.persistence.PersistenceException;
import com.yukthitech.utils.MessageFormatter;

/**
 * Will be thrown when invalid configuration is found on repository
 * @author akiran
 */
public class InvalidRepositoryException extends PersistenceException
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new invalid repository exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public InvalidRepositoryException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Instantiates a new invalid repository exception. With argumentation message support.
	 *
	 * @param cause the cause
	 * @param message the message with argument params
	 * @param args the arguments for message
	 */
	public InvalidRepositoryException(Throwable cause, String message, Object... args)
	{
		super(MessageFormatter.format(message, args), cause);
	}

	/**
	 * Instantiates a new invalid repository exception.
	 *
	 * @param message the message with argument params
	 * @param args the arguments for message
	 */
	public InvalidRepositoryException(String message, Object... args)
	{
		super(MessageFormatter.format(message, args));
	}
	
}
