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

import org.apache.commons.beanutils.ConversionException;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.utils.test.ITestGroups;

/**
 * 
 * @author akiran
 */
public class TDataConverter
{
	
	@Test(groups = ITestGroups.UNIT_TESTS)
	public void testSimpleConversions()
	{
		DataConverter converter = new DataConverter();
		
		Assert.assertEquals(34, (int)converter.convert("34", int.class));
		Assert.assertEquals("34", converter.convert(34, String.class));
		Assert.assertEquals(34.3f, converter.convert("34.3", float.class));
		Assert.assertEquals((short)34, (short)converter.convert("34", short.class));
		Assert.assertEquals(34.3, converter.convert("34.3", double.class));
		Assert.assertEquals("MALE", converter.convert(Gender.MALE, String.class));
		Assert.assertEquals(Gender.MALE, converter.convert("MALE", Gender.class));
		
		Assert.assertEquals(null, converter.convert(null, Gender.class));
	}
	
	@Test(groups = ITestGroups.UNIT_TESTS, expectedExceptions = ConversionException.class)
	public void testErrorInt()
	{
		DataConverter converter = new DataConverter();
		
		converter.convert("34ds", int.class);
	}

	@Test(groups = ITestGroups.UNIT_TESTS, expectedExceptions = ConversionException.class)
	public void testErrorEnum()
	{
		DataConverter converter = new DataConverter();
		
		converter.convert(10, Gender.class);
	}
}
