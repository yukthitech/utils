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
package com.yukthitech.persistence.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Orm common utils.
 * @author akiran
 */
public class OrmUtils
{
	/**
	 * Creates collection of specified type.
	 * @param collectionType
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Collection<Object> createCollection(Class<?> collectionType)
	{
		//check if instance of collection can be created directly from collection type
		try
		{
			return (Collection) collectionType.getConstructor().newInstance();
		}catch(Exception ex)
		{
		}

		//if not use abstraction types and determine the type to be used
		if(List.class.isAssignableFrom(collectionType))
		{
			collectionType = ArrayList.class;
		}
		else if(Set.class.isAssignableFrom(collectionType))
		{
			collectionType = HashSet.class;
		}

		try
		{
			return (Collection) collectionType.getConstructor().newInstance();
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while creating collection of type: " + collectionType.getName(), ex);
		}
	}
	
	/**
	 * For each field declared in type (and for fields declared in its super class) consumer
	 * will be invoked.
	 * @param type
	 * @param consumer
	 */
	public static void processFields(Class<?> type, Consumer<Field> consumer)
	{
		Field fields[] = null;
		
		while(!Object.class.equals(type))
		{
			fields = type.getDeclaredFields();
			
			for(Field field : fields)
			{
				consumer.accept(field);
			}
			
			type = type.getSuperclass();
		}
	}
}
