package com.yukthi.utils.fmarker;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.utils.CommonUtils;

public class TFreeMarkerEngine
{
	@Test
	public void testMethodLoading()
	{
		FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
		freeMarkerEngine.loadClass(TestMethods.class);
		
		String str = "Some string values ${var1} ${var2} and ${test()} ${sum(2,3)}";
		str = freeMarkerEngine.processTemplate("Test", str, CommonUtils.toMap("var1", "val1", "var2", "val2"));
		
		Assert.assertEquals(str, "Some string values val1 val2 and test 5");
	}
}
