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
package com.yukthitech.utils.cli;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to map a field of a bean from command line argument. 
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CliArgument
{
	/**
	 * Name of the argument.
	 * @return argument name
	 */
	public String name();
	
	/**
	 * Long name of the argument.
	 * @return long argument name.
	 */
	public String longName() default "";
	
	/**
	 * Description of the argument.
	 * @return description
	 */
	public String description();
	
	/**
	 * Specifies if this argument is mandatory. Defaults to true.
	 * @return true if mandatory.
	 */
	public boolean required() default true;
}
