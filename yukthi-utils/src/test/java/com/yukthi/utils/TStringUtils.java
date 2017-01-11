package com.yukthi.utils;

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
}
