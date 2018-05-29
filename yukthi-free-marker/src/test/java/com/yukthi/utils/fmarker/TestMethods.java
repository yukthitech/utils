package com.yukthi.utils.fmarker;

import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

public class TestMethods
{
	@FreeMarkerMethod
	public static String test()
	{
		return "test";
	}
	
	@FreeMarkerMethod("sum")
	public static int add(int i1, int i2)
	{
		return i1 + i2;
	}
}
