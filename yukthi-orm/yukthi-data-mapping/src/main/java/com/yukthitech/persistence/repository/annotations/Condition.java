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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the target parameter or field as condition
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface Condition
{
	/**
	 * Name of the entity field on which this condition should be applied. If the target is
	 * finder method param, this is mandatory. If this annotation is used on field, this is
	 * optional.
	 * @return Entity field name expression
	 */
	public String value() default "";
	
	/**
	 * Operator to be used in condition
	 * @return Condition operator to be used
	 */
	public Operator op() default Operator.EQ;
	
	/**
	 * Join operator to be used for this condition
	 * @return Join operator to be used for this condition
	 */
	public JoinOperator joinWith() default JoinOperator.AND;
	
	/**
	 * Has to be used only on Boolean/boolean fields/params. 
	 * 		If set to true, then this field will be used for IS NULL check.
	 * 		If set to false or non-boolean values, then this field will be used for IS NOT NULL check.
	 * 		If null, then condition will be ignored just like any other conditions.
	 *
	 * @return true, if successful
	 */
	public boolean nullCheck() default false;
	
	/**
	 * During condition evaluation case will be ignored
	 * @return
	 */
	public boolean ignoreCase() default false;
}
