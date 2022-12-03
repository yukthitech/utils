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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author akiran
 *
 */
public class TCryptoUtils
{
	private static Logger logger = LogManager.getLogger(TCryptoUtils.class);
	
	@Test
	public void testEnryption()
	{
		String secretKey = "Ad34@#dd!%df^*d1";
		
		String input = "2323:pop:123,334,434";
		
		String encryptedStr = CryptoUtils.encrypt(secretKey, input);
		logger.debug("{} got encrypted as {}", input, encryptedStr);
		
		Assert.assertNotEquals(encryptedStr, input);
		
		String decryptedStr = CryptoUtils.decrypt(secretKey, encryptedStr);
		Assert.assertEquals(decryptedStr, input);
	}
}
