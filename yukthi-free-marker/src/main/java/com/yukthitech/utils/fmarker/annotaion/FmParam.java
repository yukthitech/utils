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
 * Used to give description of free marker method parameter.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface FmParam
{
	/**
	 * Name of the parameter.
	 * @return
	 */
	public String name();
	
	/**
	 * Description of the parameter.
	 * @return
	 */
	public String description();
	
	/**
	 * Specified default value assumed by this param.
	 * @return default value
	 */
	public String defaultValue() default "";
	
	/**
	 * Used for directives. Indicates this parameter should be used to pass body
	 * content into this parameter.
	 * @return
	 */
	public boolean body() default false;
	
	/**
	 * Used for directives and can be used only on Map params. Indicates this parameter should be used to pass all parameter map.
	 * This is useful when directive works with dynamic parameters.
	 * @return
	 */
	public boolean allParams() default false;
}
