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
package com.yukthitech.utils.doc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 */
public class MethodDoc extends BaseDoc<MethodDoc>
{
	/**
	 * Return type of this method.
	 */
	private String returnType;
	
	/**
	 * Return description of this method.
	 */
	private String returnDescription;
	
	/**
	 * Parameters documentation.
	 */
	private List<ParamDoc> parameters;

	/**
	 * Gets the return type of this method.
	 *
	 * @return the return type of this method
	 */
	public String getReturnType()
	{
		if(returnType == null)
		{
			return "void";
		}
		
		return returnType;
	}

	/**
	 * Sets the return type of this method.
	 *
	 * @param returnType the new return type of this method
	 */
	public MethodDoc setReturnType(String returnType)
	{
		this.returnType = returnType;
		return this;
	}

	/**
	 * Gets the return description of this method.
	 *
	 * @return the return description of this method
	 */
	public String getReturnDescription()
	{
		return returnDescription;
	}

	/**
	 * Sets the return description of this method.
	 *
	 * @param returnDescription the new return description of this method
	 */
	public MethodDoc setReturnDescription(String returnDescription)
	{
		if(returnDescription == null || returnDescription.trim().length() == 0)
		{
			returnDescription = null;
		}
		
		this.returnDescription = returnDescription;
		return this;
	}

	/**
	 * Gets the parameters documentation.
	 *
	 * @return the parameters documentation
	 */
	public List<ParamDoc> getParameters()
	{
		if(parameters == null)
		{
			return Collections.emptyList();
		}
		
		return parameters;
	}

	/**
	 * Sets the parameters documentation.
	 *
	 * @param parameters the new parameters documentation
	 */
	public MethodDoc setParameters(List<ParamDoc> parameters)
	{
		this.parameters = parameters;
		return this;
	}
	
	/** 
	 * Adds value to {@link #parameters parameters}
	 *
	 * @param param param to be added
	 */
	 public MethodDoc addParameter(ParamDoc param)
	 {
		 if(parameters == null)
		 {
			 parameters = new ArrayList<ParamDoc>();
		 }
		 
		 parameters.add(param);
		 return this;
	 }
	 
	 public List<String> getParamSignatures()
	 {
		 if(parameters == null || parameters.isEmpty())
		 {
			 return null;
		 }
		 
		 return parameters.stream()
				 .map(param -> param.getType() + " " + param.getName())
				 .collect(Collectors.toList());
	 }
}
