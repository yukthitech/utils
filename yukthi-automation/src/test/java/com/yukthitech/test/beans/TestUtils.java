package com.yukthitech.test.beans;

import com.yukthitech.utils.exceptions.InvalidStateException;

public class TestUtils
{
	public int halfOf(int val)
	{
		if(val <= 0)
		{
			throw new InvalidStateException("Value can not be less than zero: {}", val);
		}
		
		return val / 2;
	}
}
