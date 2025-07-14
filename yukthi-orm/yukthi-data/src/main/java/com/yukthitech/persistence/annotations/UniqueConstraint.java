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
package com.yukthitech.persistence.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents unique constraint
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface UniqueConstraint
{
	/**
	 * Name of the constraint
	 * @return
	 */
	public String name();
	
	/**
	 * Fields on which unique constraint needs to be maintained
	 * @return
	 */
	public String[] fields() default {};
	
	/**
	 * Error message to be used when constraint fails
	 * @return
	 */
	public String message() default "";
	
	/**
	 * Flag to indicate whether this constraint has to be validated
	 * before insert or update
	 * @return
	 */
	public boolean validate() default true;
	
	/**
	 * Indicates that specified constraint name is final name. And framework 
	 * should not prefix or suffix with anything else. Default: true.
	 * @return true if specified name is final one.
	 */
	public boolean finalName() default true;
}
