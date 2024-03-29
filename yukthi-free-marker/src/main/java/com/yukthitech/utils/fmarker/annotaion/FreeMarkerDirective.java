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
package com.yukthitech.utils.fmarker.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks target method as free marker directive.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FreeMarkerDirective
{
	/**
	 * Name using which target method can be accessed from free marker templates as directive.
	 * Defaults to target method name.
	 * @return
	 */
	public String value() default "";
	
	/**
	 * Description of this directive.
	 * @return description.
	 */
	public String description() default "";
	
	/**
	 * Used to document example for the method.
	 * @return example doc
	 */
	public ExampleDoc[] examples() default {};
	
}
