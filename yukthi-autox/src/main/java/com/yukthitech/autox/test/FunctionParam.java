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
package com.yukthitech.autox.test;

import java.io.Serializable;

import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;

/**
 * Parameter that can be passed during step group execution.
 * @author akiran
 */
public class FunctionParam implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the group param.
	 */
	private String name;
	
	/**
	 * Value of the param.
	 */
	@Param(description = "Value of the parameter", sourceType = SourceType.EXPRESSION)
	private Object value;
	
	public FunctionParam()
	{}
	
	public FunctionParam(String name, Object value)
	{
		this.name = name;
		this.value = value;
	}

	/**
	 * Gets the name of the group param.
	 *
	 * @return the name of the group param
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the group param.
	 *
	 * @param name the new name of the group param
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the value of the param.
	 *
	 * @return the value of the param
	 */
	public Object getValue()
	{
		return value;
	}
	
	/**
	 * Sets the value of the param.
	 *
	 * @param value the new value of the param
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("{").append(name).append("=").append(value).append("}");
		return builder.toString();
	}

}
