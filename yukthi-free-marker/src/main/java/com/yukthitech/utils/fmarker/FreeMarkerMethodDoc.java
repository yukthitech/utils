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

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.utils.fmarker.annotaion.ExampleDoc;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

/**
 * Documentation of free marker method.
 * 
 * @author akiran
 */
public class FreeMarkerMethodDoc implements Comparable<FreeMarkerMethodDoc>
{
	/**
	 * Name of the document.
	 */
	private String name;

	/**
	 * Return type of the function.
	 */
	private String returnType;

	/**
	 * Description about the method.
	 */
	private String description;
	
	/**
	 * Description of return value.
	 */
	private String returnDescription;
	
	/**
	 * Parameters of the method.
	 */
	private List<ParamDoc> parameters;
	
	/**
	 * Examples of usage.
	 */
	private List<FreeMarkerMethodExampleDoc> examples;
	
	private String parameterString;
	
	private Method method;
	
	public FreeMarkerMethodDoc()
	{}

	/**
	 * Instantiates a new free marker method doc.
	 *
	 * @param name the name
	 * @param returnType the return type
	 * @param description the description
	 */
	public FreeMarkerMethodDoc(Method method)
	{
		FreeMarkerMethod freeMarkerMethod = method.getAnnotation(FreeMarkerMethod.class);
		
		this.name = StringUtils.isNotBlank(freeMarkerMethod.value()) ? freeMarkerMethod.value() : method.getName();
		this.returnType = method.getReturnType().getName();
		this.description = freeMarkerMethod.description();
		this.returnDescription = freeMarkerMethod.returnDescription();
		this.method = method;
		
		Parameter params[] = method.getParameters();
		
		for(Parameter param : params)
		{
			addParameter(new ParamDoc(param));
		}
		
		if(freeMarkerMethod.examples().length > 0)
		{
			this.examples = new ArrayList<FreeMarkerMethodExampleDoc>();
			
			for(ExampleDoc doc : freeMarkerMethod.examples())
			{
				this.examples.add(new FreeMarkerMethodExampleDoc(doc));
			}
		}
	}
	
	/**
	 * Adss the parameter details to this method doc.
	 * @param param
	 */
	public void addParameter(ParamDoc param)
	{
		if(this.parameters == null)
		{
			this.parameters = new ArrayList<ParamDoc>();
		}
		
		this.parameters.add(param);
	}
	
	public List<ParamDoc> getParameters()
	{
		return parameters;
	}

	/**
	 * Gets the name of the document.
	 *
	 * @return the name of the document
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the return type of the function.
	 *
	 * @return the return type of the function
	 */
	public String getReturnType()
	{
		return returnType;
	}

	/**
	 * Gets the description about the method.
	 *
	 * @return the description about the method
	 */
	public String getDescription()
	{
		return description;
	}
	
	/**
	 * Return description of the method.
	 * @return return description
	 */
	public String getReturnDescription()
	{
		return returnDescription;
	}
	
	/**
	 * Gets the examples of usage.
	 *
	 * @return the examples of usage
	 */
	public List<FreeMarkerMethodExampleDoc> getExamples()
	{
		return examples;
	}
	
	/**
	 * Checks if examples are available.
	 * @return
	 */
	public boolean hasExamples()
	{
		return (examples != null && examples.size() > 0);
	}
	
	/**
	 * Returns true if parameters are available.
	 * @return true if parameters available
	 */
	public boolean hasParameters()
	{
		return (parameters != null && parameters.size() > 0);
	}
	
	public String getParameterString()
	{
		if(parameterString != null)
		{
			return parameterString;
		}
		
		if(parameters == null || parameters.size() == 0)
		{
			parameterString = "()";
			return parameterString;
		}
		
		StringBuilder builder = new StringBuilder("(");
		
		for(ParamDoc param : parameters)
		{
			builder.append(param.getName()).append(", ");
		}
		
		builder.delete(builder.length() - 2, builder.length() - 1);
		builder.append(")");
		
		parameterString = builder.toString();
		return parameterString;
	}
	
	public Method getMethod()
	{
		return method;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(FreeMarkerMethodDoc o)
	{
		return name.compareTo(o.name);
	}
}
