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
package com.yukthitech.utils.expr;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Helps in marking function as expression function and provides information about the function.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FunctionInfo
{
	/**
	 * Name of the function.
	 * @return Name of the function
	 */
	public String name() default "";
	
	/**
	 * If the return type of the function, depends on the parameters then 
	 * this parameter to be used to specify the parameter list on which function return
	 * type is based. In case of distinct parameters types, the common base class will be used
	 * as return type.
	 * @return Parameter indexes whose type should be considered.
	 */
	public int[] matchParameterTypes() default {};
	
	/**
	 * Simple description about the function.
	 * @return Description about the function.
	 */
	public String description();
	
	/**
	 * Syntax of the function.
	 * @return function syntax.
	 */
	public String syntax();
}
