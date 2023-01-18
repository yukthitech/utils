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
package com.yukthitech.utils.fmarker;

import java.lang.reflect.Parameter;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.utils.fmarker.annotaion.FmParam;

/**
 * Parameter documentation.
 * @author akiran
 */
public class ParamDoc implements Comparable<ParamDoc>
{
	/**
	 * Name of the parameter.
	 */
	private String name;
	
	/**
	 * Type of the parameter.
	 */
	private String type;
	
	/**
	 * Description about the parameter.
	 */
	private String description;
	
	public ParamDoc()
	{}

	public ParamDoc(Parameter param)
	{
		FmParam fmParam = param.getAnnotation(FmParam.class);
		
		this.name = (fmParam != null && StringUtils.isNotBlank(fmParam.name())) ? fmParam.name() : param.getName();
		this.description = (fmParam != null) ? fmParam.description() : "";
		
		Class<?> type = param.getType();
		
		if(type.isArray())
		{
			this.type = type.getComponentType().getName() + "[]";
			
			if(param.isVarArgs())
			{
				this.type += " (var-args)";
			}
		}
		else
		{
			this.type = param.getType().getName();
		}
	}

	/**
	 * Gets the name of the parameter.
	 *
	 * @return the name of the parameter
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the parameter.
	 *
	 * @param name the new name of the parameter
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the type of the parameter.
	 *
	 * @return the type of the parameter
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Sets the type of the parameter.
	 *
	 * @param type the new type of the parameter
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * Gets the description about the parameter.
	 *
	 * @return the description about the parameter
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description about the parameter.
	 *
	 * @param description the new description about the parameter
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	@Override
	public int compareTo(ParamDoc o)
	{
		return name.compareTo(o.name);
	}
}
