package com.yukthi.persistence.conversion.impl;

import com.yukthi.persistence.annotations.DataType;
import com.yukthi.persistence.conversion.IPersistenceConverter;
import com.yukthi.persistence.utils.PasswordEncryptor;

public class PasswordEncryptionConverter implements IPersistenceConverter
{
	@Override
	public Object convertToJavaType(Object dbObject, DataType dbType, Class<?> javaType)
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
