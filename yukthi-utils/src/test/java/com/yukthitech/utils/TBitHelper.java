package com.yukthitech.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test cases for bit helper.
 * @author akiran
 */
public class TBitHelper
{
	@Test
	public void testBitHelper()
	{
		final int BIT_0 = 1;
		final int BIT_1 = 2;
		final int BIT_2 = 4;
		
		int flags = 0;
		
		//check basic working of isSet
		Assert.assertFalse(BitHelper.isSet(flags, BIT_0));
		Assert.assertFalse(BitHelper.isSet(flags, BIT_1));
		Assert.assertFalse(BitHelper.isSet(flags, BIT_2));
		
		//set the flags and ensure isSet is working
		flags = BitHelper.setFlag(flags, BIT_0);
		flags = BitHelper.setFlag(flags, BIT_1);

		Assert.assertTrue(BitHelper.isSet(flags, BIT_0));
		Assert.assertTrue(BitHelper.isSet(flags, BIT_1));
		Assert.assertFalse(BitHelper.isSet(flags, BIT_2));

		//unset a flag and ensure with isSet only that flag is unset
		flags = BitHelper.unsetFlag(flags, BIT_1);

		Assert.assertTrue(BitHelper.isSet(flags, BIT_0));
		Assert.assertFalse(BitHelper.isSet(flags, BIT_1));
		Assert.assertFalse(BitHelper.isSet(flags, BIT_2));
	}
}
