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

import com.yukthitech.persistence.repository.annotations.Charset;

/**
 * Marks the target entity type as extendable and provides extension attributes.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface Extendable
{
	/**
	 * Name of the table where extended fields should be maintained. By default, 
	 * they are maintained in table with name EXT_[entity-table-name].
	 * @return extended fields table name
	 */
	public String tableName() default "";
	
	/**
	 * Extended field prefix. By default it is "field", so fields will be named as field0, field1, field2... so on.
	 * @return Extended field prefix.
	 */
	public String fieldPrefix() default "field";
	
	/**
	 * Number of extension fields.
	 * @return number of extension fields.
	 */
	public int count();
	
	/**
	 * Size of each extension field. This will in turn defines the maximum size of the value in 
	 * an extension field.
	 * @return size of extension field.
	 */
	public int fieldSize();
	
	/**
	 * Character set for extension field data. Default is LATIN1.
	 * @return Character set to be used.
	 */
	public Charset charset() default Charset.LATIN1;
}
