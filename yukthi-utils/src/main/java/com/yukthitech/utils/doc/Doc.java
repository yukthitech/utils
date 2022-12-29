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

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Generic annotation that can be used to document any common elements.
 * @author akranthikiran
 */
@Retention(RUNTIME)
@Target({ TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR })
public @interface Doc
{
	/**
	 * Field which can be used to group different documentations.
	 * @return
	 */
	public String group() default "";
	
	/**
	 * Name of this element to be used in doc.
	 * @return
	 */
	public String name() default "";
	
	/**
	 * Documentation for target element.
	 * @return
	 */
	public String value();
	
	/**
	 * Examples for reference.
	 * @return
	 */
	public String[] examples() default {};
	
	/**
	 * Description for return values, in case of methods.
	 * @return
	 */
	public String returnDesc() default "";
}
