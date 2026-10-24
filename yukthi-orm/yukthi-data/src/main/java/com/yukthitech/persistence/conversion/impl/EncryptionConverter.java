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
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.conversion.ConverterContext;
import com.yukthitech.persistence.conversion.IPersistenceConverter;
import com.yukthitech.utils.Encryptor;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Encrypts values while persisting and decrypts them while reading, using the
 * {@link Encryptor} configured on the data store.
 * 
 * This is not a default converter. Use it explicitly on entity fields with {@link DataTypeMapping}.
 * @author akiran
 */
public class EncryptionConverter implements IPersistenceConverter
{
	@Override
	public Object convertToJavaType(ConverterContext context, Object dbObject, DataType dbType, Class<?> javaType, Field field)
	{
		if(dbObject == null)
		{
			return null;
		}

		return getEncryptor(context).decrypt(dbObject.toString());
	}

	@Override
	public Object convertToDBType(ConverterContext context, Object javaObject, DataType dbType)
	{
		if(javaObject == null)
		{
			return null;
		}

		return getEncryptor(context).encrypt(javaObject.toString());
	}

	private Encryptor getEncryptor(ConverterContext context)
	{
		if(context == null || context.getEncryptor() == null)
		{
			throw new InvalidStateException("Encryptor is not configured");
		}

		return context.getEncryptor();
	}
}
