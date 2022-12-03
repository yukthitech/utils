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

/**
 * Represents function that can be used in expressions.
 * @author akiran
 */
public interface IFunction
{
	/**
	 * Name of the function.
	 * @return name of the function.
	 */
	public String getName();
	
	/**
	 * Syntax of the function.
	 * @return syntax of the function.
	 */
	public String getSyntax();
	
	/**
	 * Description of the function.
	 * @return Description of the function.
	 */
	public String getDescription();
	
	/**
	 * Fetches the would be return type based on parameter types specified.
	 * @param paramTypes Parameter types.
	 * @return Would be return type based on parameter types.
	 */
	public Class<?> getReturnType(Class<?> paramTypes[]);
	
	/**
	 * Evaluates current function with specified parameter values.
	 * @param parameters Parameter values.
	 * @return Result value.
	 */
	public Object evaluate(Object parameters[]);
}
