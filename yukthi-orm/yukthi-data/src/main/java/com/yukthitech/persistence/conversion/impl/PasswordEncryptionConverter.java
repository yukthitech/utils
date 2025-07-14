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
package com.yukthitech.persistence.conversion.impl;

import java.lang.reflect.Field;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.conversion.IPersistenceConverter;
import com.yukthitech.persistence.utils.PasswordEncryptor;

public class PasswordEncryptionConverter implements IPersistenceConverter
{
	@Override
	public Object convertToJavaType(Object dbObject, DataType dbType, Class<?> javaType, Field field)
	{
		//Retain encrypted value from db. As encryption is one way encryption
		return dbObject;
	}

	@Override
	public Object convertToDBType(Object javaObject, DataType dbType)
	{
		return PasswordEncryptor.encryptPassword((String)javaObject);
	}

}
