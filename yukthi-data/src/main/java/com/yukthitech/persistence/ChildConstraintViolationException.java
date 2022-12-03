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

public class ChildConstraintViolationException extends PersistenceException
{
	private static final long serialVersionUID = 1L;
	
	private String constraintName;

	public ChildConstraintViolationException(String constraintName, String message, Throwable cause)
	{
		super(message, cause);
		this.constraintName = constraintName;
	}

	public ChildConstraintViolationException(String constraintName, String message)
	{
		super(message);
		this.constraintName = constraintName;
	}
	
	public String getConstraintName()
	{
		return constraintName;
	}
}
