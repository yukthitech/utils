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
package com.yukthitech.persistence.repository.annotations;

public enum Operator
{
	EQ("=", true), 
	LT("<"), 
	LE("<="), 
	GT(">"), 
	GE(">="), 
	NE("!=", true), 
	LIKE("LIKE"), 
	IN("IN"), 
	NOT_IN("NOT IN"), 

	;
	private String operator;
	
	/**
	 * Indicates whether operator supports null values
	 */
	private boolean nullable;
	
	private Operator(String operator)
	{
		this.operator = operator;
	}

	private Operator(String operator, boolean nullable)
	{
		this.operator = operator;
		this.nullable = nullable;
	}
	
	public boolean isNullable()
	{
		return nullable;
	}
	
	public String getOperator()
	{
		return operator;
	}

	@Override
	public String toString()
	{
		return operator;
	}
	
	
}
