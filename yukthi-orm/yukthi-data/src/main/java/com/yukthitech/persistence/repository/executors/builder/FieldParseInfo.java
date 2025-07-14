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
package com.yukthitech.persistence.repository.executors.builder;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.FieldDetails;

/**
 * Intermediate table used while parsing expressions.
 * @author akiran
 */
class FieldParseInfo
{
	/**
	 * Target entity details.
	 */
	public EntityDetails entityDetails;
	
	/**
	 * Field or parameter (in query or result bean/parameter) whose expression
	 * is being parsed.
	 */
	public String sourceField;
	
	/**
	 * Expression to be parsed.
	 */
	public String expression;
	
	/**
	 * Entity field path parts.
	 */
	public String entityFieldPath[];
	
	/**
	 * Description of the repository method.
	 */
	public String methodDesc;
	
	/**
	 * Final field details represented by this expression.
	 */
	public FieldDetails fieldDetails;
	
}
