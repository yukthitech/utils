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
package com.yukthitech.validation.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yukthitech.validation.cross.CrossConstraint;
import com.yukthitech.validators.LessThanValidator;

/**
 * Adds constraint so that target field value is less than value of specified field by {@link #field()}.
 * Can be used on Number of Date fields.
 * @author akiran
 */
@Documented
@CrossConstraint(validatedBy = LessThanValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface LessThan
{
	/**
	 * Field with which current field should be compared
	 * @return
	 */
	public String field();
	
	public String message() default "{com.yukthitech.validation.annotations.LessThan}";
}
