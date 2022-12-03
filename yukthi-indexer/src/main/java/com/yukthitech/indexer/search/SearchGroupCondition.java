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
package com.yukthitech.indexer.search;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates target field should be used as subquery.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SearchGroupCondition
{
	/**
	 * Parent field name to use for all sub conditions. By default
	 * no parent field name will be attached.
	 * @return
	 */
	public String parentField() default "";
	
	/**
	 * Join operator under which this condition should be grouped.
	 * @return
	 */
	public JoinOperator joinWith() default JoinOperator.DEFAULT;

	/**
	 * Default join operator to be used between conditions of this subgroup.
	 * @return
	 */
	public JoinOperator defaultJoinOp() default JoinOperator.AND;
}
