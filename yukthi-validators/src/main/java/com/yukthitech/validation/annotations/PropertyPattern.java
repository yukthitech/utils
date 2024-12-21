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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yukthitech.validators.PropertyPatternValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Can be used to specify pattern on sub field.
 * @author akiran
 */
@Constraint(validatedBy = PropertyPatternValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyPattern
{
	public String message() default "{com.yukthitech.validation.annotations.PropertyPattern}";

	public Class<?>[] groups() default {};

	public Class<? extends Payload>[] payload() default {};

	/**
	 * Property on which pattern has to be implemented.
	 * @return
	 */
	public String property();

	/**
	 * Regular expression to match.
	 * @return
	 */
	public String regexp();
}
