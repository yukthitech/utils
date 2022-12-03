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
 * @author akiran
 *
 */
public class TConvertUtils
{
	/**
	 * Test positive cases
	 */
	@Test(groups = ITestGroups.UNIT_TESTS)
	public void testConvert()
	{
		//invoking constructor for coverage purpose
		new ConvertUtils();
		
		//try to convert from string boolean
		Assert.assertEquals(ConvertUtils.convert("true", Boolean.class), Boolean.TRUE);
		Assert.assertEquals(ConvertUtils.convert("true", boolean.class), true);
		
		Assert.assertEquals(ConvertUtils.convert("false", Boolean.class), Boolean.FALSE);
		
		//string to int
		Assert.assertEquals(ConvertUtils.convert("123", int.class), 123);
		
		//long to int
		Assert.assertEquals(ConvertUtils.convert(134L, int.class), 134);
		
		//int to int
		Assert.assertEquals(ConvertUtils.convert(123, int.class), 123);
		
		//string to float
		Assert.assertEquals(ConvertUtils.convert("3.45", float.class), 3.45f);
		
		//string to enum
		Assert.assertEquals(ConvertUtils.convert("MALE", Gender.class), Gender.MALE);
		Assert.assertEquals(ConvertUtils.convert(Gender.MALE, String.class), "MALE");
	}
	
	/**
	 * Test when invalid value specified for int conversion
	 */
	@Test(groups = ITestGroups.UNIT_TESTS, expectedExceptions = ConversionException.class)
	public void testFailure_int()
	{
		ConvertUtils.convert("abc", int.class);
	}

	/**
	 * Test when invalid value specified for boolean conversion
	 */
	@Test(groups = ITestGroups.UNIT_TESTS, expectedExceptions = ConversionException.class)
	public void testFailure_boolean()
	{
		ConvertUtils.convert("abc", Boolean.class);
	}
}
