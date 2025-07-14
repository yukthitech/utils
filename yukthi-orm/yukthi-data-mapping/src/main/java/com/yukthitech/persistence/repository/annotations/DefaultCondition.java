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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used to null conditions to target query method
 * 
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultCondition
{
	/**
	 * Field for which null condition has to be checked. 
	 * @return field to check for null
	 */
	public String field();
	
	/**
	 * Operator to be used in condition
	 * @return Condition operator to be used
	 */
	public Operator op() default Operator.EQ;

	/**
	 * Value for the condition
	 * @return Value for the condition
	 */
	public String value();
	
	/**
	 * Join operator which would be used to join this condition to other conditions
	 * @return jooin operator
	 */
	public JoinOperator joinOperator() default JoinOperator.AND;
}
