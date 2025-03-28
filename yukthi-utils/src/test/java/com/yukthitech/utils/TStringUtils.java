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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test cases for string utils.
 * @author akiran
 */
public class TStringUtils
{
	/**
	 * Test toStartLower() works as expected.
	 */
	@Test
	public void testToStartLower()
	{
		Assert.assertEquals(StringUtils.toStartLower("StringUtils"), "stringUtils");
		Assert.assertEquals(StringUtils.toStartLower("_StringUtils"), "_StringUtils");
	}
	
	@Test
	public void testRandomAlphaNumericString()
	{
		String randString = StringUtils.randomAlphaNumericString(10, '$', '@', '%');
		System.out.println(randString);
		
		Assert.assertEquals(randString.length(), 10);
	}
	
	@Test
	public void testTrimLines()
	{
		Assert.assertEquals(
				StringUtils.trimLines("  Line 1  \n   Line 2 \nLine 3\n  Line 4\n Line 5"), 
				"Line 1\n Line 2\nLine 3\nLine 4\nLine 5");

		Assert.assertEquals(
				StringUtils.trimLines("  Line 1  \n\n   Line 2\n"), 
				"Line 1\n\n Line 2");
	}
}
