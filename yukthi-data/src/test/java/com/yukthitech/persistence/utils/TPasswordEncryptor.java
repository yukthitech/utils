package com.yukthitech.persistence.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.persistence.utils.PasswordEncryptor;

public class TPasswordEncryptor
{
	@Test
	public void testEncryption()
	{
		String password = "testPassword@123";
		String encryptedValue = PasswordEncryptor.encryptPassword(password);
		
		System.out.println("Encrypted value is  - "  + encryptedValue);
		
		Assert.assertNotEquals(password, encryptedValue);
		Assert.assertTrue(PasswordEncryptor.isSamePassword(encryptedValue, password));
	}
}
