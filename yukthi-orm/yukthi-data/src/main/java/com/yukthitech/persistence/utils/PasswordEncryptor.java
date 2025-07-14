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

import org.apache.commons.lang3.StringUtils;
import org.jasypt.util.password.StrongPasswordEncryptor;

/**
 * Utility to encrypt password and to validate plain password against encrypted password 
 * @author akiran
 */
public class PasswordEncryptor
{
	/**
	 * Password encryption algorithm
	 */
	private static StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
	
	/**
	 * Encrypts the specified password
	 * @param password plain text password
	 * @return Encrypted password
	 */
	public static String encryptPassword(String password)
	{
		if(StringUtils.isEmpty(password))
		{
			return null;
		}
		
		return passwordEncryptor.encryptPassword(password);
	}
	
	/**
	 * Checks and returns specified plain text password is same as encrypted password
	 * @param encryptedPassword
	 * @param plainPassword
	 * @return True is encrypted password same as plain text password
	 */
	public static boolean isSamePassword(String encryptedPassword, String plainPassword)
	{
		//if either of passwords are empty
		if(StringUtils.isEmpty(encryptedPassword) || StringUtils.isEmpty(plainPassword))
		{
			return false;
		}
		
		return passwordEncryptor.checkPassword(plainPassword, encryptedPassword);
	}
	
	public static void main(String[] args)
	{
		String password = args[0];
		System.out.println(encryptPassword(password));
	}
}
