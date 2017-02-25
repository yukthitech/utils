package com.yukthitech.test;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.ccg.xml.XMLUtil;

public class TXmlUtil
{
	@Test
	public void testReplaceHypens()
	{
		Assert.assertEquals(XMLUtil.replaceHyphens("abc-def"), "abcDef");
		Assert.assertEquals(XMLUtil.replaceHyphens("abc--def"), "abcDef");
	}
}
