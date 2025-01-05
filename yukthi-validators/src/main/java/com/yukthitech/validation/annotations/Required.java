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

import com.yukthitech.validators.RequiredValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Ensures some or other value is provided for target field. For String field ensures non-zero length is provided.
 * For number fields, ensures non-zero value is provided.
 * @author akiran
 */
@Constraint(validatedBy = RequiredValidator.class)
@Target( { ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Required
{
	public String message() default "{com.yukthitech.validation.annotations.Required}";

	public Class<?>[] groups() default {};

	public Class<? extends Payload>[] payload() default {};

}
