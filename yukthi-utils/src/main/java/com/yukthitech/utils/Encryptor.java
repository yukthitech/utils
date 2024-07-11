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

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.Provider;
import java.security.cert.X509Certificate;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Utility to encrypt or decrypt using keystore.
 * 
 * Use below command to generate new keystore files:
 * 		keytool -genkey -keyalg RSA -validity 3650 -sigalg SHA256withRSA -alias <alias> -keystore <keystore-file> -storepass <pwd> -keypass <pwd>
 */
public class Encryptor
{
	private static String RSA_ECB_PKCS5 = "RSA/ECB/PKCS1Padding";

	private String keyStorePassword;
	private Key privateKey;
	private Key publicKey;

	public Encryptor(String keyStoreFile, String alias, String password)
	{
		FileInputStream fis = null;
		
		try
		{
			fis = new FileInputStream(keyStoreFile);
			init(fis, alias, password);
		}catch(Exception e)
		{
			throw new InvalidStateException("An error occurred while opening key-store file - " + keyStoreFile, e);
		}
	}

	public Encryptor(InputStream is, String alias, String password)
	{
		init(is, alias, password);
	}

	private void init(InputStream input, String alias, String password)
	{
		this.keyStorePassword = password;

		try
		{
			final KeyStore keyStore = KeyStore.getInstance("PKCS12");

			keyStore.load(input, keyStorePassword.toCharArray());
			input.close();

			privateKey = keyStore.getKey(alias, keyStorePassword.toCharArray());

			if(privateKey == null)
			{
				throw new InvalidStateException("Failed to load encryption key with alias - " + alias);
			}

			X509Certificate cert = null;
			cert = (X509Certificate)keyStore.getCertificate(alias);
			publicKey = cert.getPublicKey();
		}catch(Exception e)
		{
			throw new InvalidStateException("An error occurred while initiating config-encryptor", e);
		}
	}

	private static Key transformKey(Key key, String algorithm, Provider provider) throws Exception
	{
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm, provider);
		return keyFactory.translateKey(key);
	}

	/* (non-Javadoc)
	 * @see org.jasypt.encryption.StringEncryptor#decrypt(java.lang.String)
	 */
	public String decrypt(String cypher)
	{
		String decryptedString = cypher;

		try
		{
			Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS5);
			cipher.init(Cipher.DECRYPT_MODE, transformKey(privateKey, "RSA", new BouncyCastleProvider()));
			decryptedString = new String(cipher.doFinal(Base64.decodeBase64(cypher.getBytes())));
		}catch(Exception e)
		{
			throw new IllegalStateException("Failed to decrypt the encoded string", e);
		}

		return decryptedString;
	}

	/* (non-Javadoc)
	 * @see org.jasypt.encryption.StringEncryptor#encrypt(java.lang.String)
	 */
	public String encrypt(String plainText)
	{
		String encryptedString = null;
		try
		{
			Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS5);

			cipher.init(Cipher.ENCRYPT_MODE, transformKey(publicKey, "RSA", new BouncyCastleProvider()));

			encryptedString = new String(Base64.encodeBase64(cipher.doFinal(plainText.getBytes())));
		}catch(Exception e)
		{
			throw new IllegalStateException("Error occuurred while trying to encrypt.", e);
		}

		return encryptedString;
	}
	
	public static void main(String[] args)
	{
		if(args.length != 5)
		{
			throw new IllegalArgumentException("Usage: Encryptor <keystore file> <alias> <password> [encrypt/decrypt] [input]");
		}
		
		Encryptor encryptor = new Encryptor(args[0], args[1], args[2]);
		
		if("encrypt".equals(args[3]))
		{
			String encrypted = encryptor.encrypt(args[4]);
			System.out.println("Encrypted: " + encrypted);
		}
		else if("decrypt".equals(args[3]))
		{
			String decrypted = encryptor.decrypt(args[4]);
			System.out.println("Decrypted: " + decrypted);
		}
	}
}
