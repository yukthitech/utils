/**
 * 
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
