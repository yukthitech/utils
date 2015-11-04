/*
 * The MIT License (MIT)
 * Copyright (c) 2015 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yukthi.utils;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

import com.yukthi.utils.exceptions.InvalidArgumentException;

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
