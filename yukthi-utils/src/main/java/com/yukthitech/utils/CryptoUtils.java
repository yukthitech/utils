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
package com.yukthitech.utils;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Utility to encrypt/decrypt the string
 * 
 * @author akiran
 */
public class CryptoUtils
{
	/**
	 * Algorithm to use
	 */
	private static final String ALGORITHM = "AES";

	/**
	 * Transformation to use
	 */
	private static final String TRANSFORMATION = "AES";

	/**
	 * Encrypts the provided input and convert encrypted bytes into hex string
	 * @param secretKey Secret key to use for encryption
	 * @param input Input to be encrypted
	 * @return encrypted and hex converted string
	 */
	public static String encrypt(String secretKey, String input)
	{
		if(secretKey.length() != 16)
		{
			throw new InvalidArgumentException("Secret key should be of length 16. Specified secrey key length - ", secretKey.length());
		}
		
		//convert input into bytes
		byte inputBytes[] = input.getBytes();
		
		//encrypt bytes
		byte encryptedBytes[] = doCrypto(Cipher.ENCRYPT_MODE, secretKey, inputBytes);
		
		//convert encrypted bytes into hex string
		return Hex.encodeHexString(encryptedBytes);
	}

	/**
	 * Decrypts specified input. Input should be encrypted and hex string (the way it is generated
	 * by {@link #encrypt(String, String)} method
	 * @param secretKey Secret key to use for decryption
	 * @param input Input hex string to be decrypted
	 * @return Decrypted value
	 */
	public static String decrypt(String secretKey, String input)
	{
		if(secretKey.length() != 16)
		{
			throw new InvalidArgumentException("Secret key should be of length 16. Specified secrey key length - ", secretKey.length());
		}
		
		try
		{
			//decode input string hex string
			byte decodedBytes[] = Hex.decodeHex(input.toCharArray());
			
			//decrypt decoded bytes
			byte decryptedBytes[] = doCrypto(Cipher.DECRYPT_MODE, secretKey, decodedBytes);
			
			//convert and return decrypted bytes into string
			return new String(decryptedBytes);
		}catch(Exception ex)
		{
			throw new InvalidArgumentException(ex, "An error occurred while decrypting string - {}", input);
		}
	}

	/**
	 * Encrypts/Decrypts input string using AES algorithm
	 * @param cipherMode
	 * @param secretKeyStr
	 * @param input
	 * @return
	 */
	private static byte[] doCrypto(int cipherMode, String secretKeyStr, byte input[])
	{
		try
		{
			Key secretKey = new SecretKeySpec(secretKeyStr.getBytes(), ALGORITHM);
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(cipherMode, secretKey);

			return cipher.doFinal(input);
		} catch(Exception ex)
		{
			throw new InvalidArgumentException(ex, "Error encrypting/decrypting input bytes");
		}
	}
}
