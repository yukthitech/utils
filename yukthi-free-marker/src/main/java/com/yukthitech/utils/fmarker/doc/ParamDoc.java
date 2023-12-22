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
package com.yukthitech.utils.fmarker.doc;

import java.lang.reflect.Parameter;
import java.util.Map;

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
	
	/**
	 * Default value of the param.
	 */
	private String defaultValue;
	
	/**
	 * Flag indicating if this is body param.
	 */
	private boolean body;
	
	/**
	 * Flag indicating if this param accepts all param map.
	 */
	private boolean allParams;
	
	private Parameter parameter;
	
	public ParamDoc()
	{}

	public ParamDoc(Parameter param)
	{
		this(param.getAnnotation(FmParam.class), param);
	}		
		
	public ParamDoc(FmParam fmParam, Parameter param)
	{
		this.parameter = param;
		this.name = (fmParam != null && StringUtils.isNotBlank(fmParam.name())) ? fmParam.name() : param.getName();
		this.description = (fmParam != null) ? fmParam.description() : "";
		this.defaultValue = (fmParam != null) ? fmParam.defaultValue() : "";
		this.body = (fmParam != null) ? fmParam.body() : false;
		
		Class<?> type = param != null ? param.getType() : Object.class;
		
		this.allParams = fmParam != null && fmParam.allParams() && Map.class.equals(type);
		
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
			this.type = type.getName();
		}
	}
	
	public Parameter getParameter()
	{
		return parameter;
	}
	
	public boolean isAllParams()
	{
		return allParams;
	}
	
	/**
	 * Checks if is flag indicating if this is body param.
	 *
	 * @return the flag indicating if this is body param
	 */
	public boolean isBody()
	{
		return body;
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
	
	public String getDefaultValue()
	{
		return defaultValue;
	}
	
	/**
	 * Sets the default value of the param.
	 *
	 * @param defaultValue the new default value of the param
	 */
	public void setDefaultValue(String defaultValue)
	{
		this.defaultValue = defaultValue;
	}

	@Override
	public int compareTo(ParamDoc o)
	{
		return name.compareTo(o.name);
	}
}
