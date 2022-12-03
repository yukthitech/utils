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
package com.yukthitech.persistence.conversion;

import com.yukthitech.persistence.annotations.DataType;

/**
 * Interface to convert that can be used on entity fields to control the wat the value
 * is stored and read to/from db
 * @author akiran
 */
public interface IPersistenceConverter
{
	/**
	 * Converts "dbObject" read from DB (whose data type is specified "dBtype") to specified 
	 * java-type
	 * @param dbObject DB Object to be converted
	 * @param dbType Target field db type
	 * @param javaType Target field java type
	 * @return Java type converted object
	 */
	public Object convertToJavaType(Object dbObject, DataType dbType, Class<?> javaType);
	
	/**
	 * Converts specified java object into target dbType object
	 * @param javaObject Source java object that needs to be converted
	 * @param dbType Target DB type
	 * @return DB type converted object
	 */
	public Object convertToDBType(Object javaObject, DataType dbType);
}
