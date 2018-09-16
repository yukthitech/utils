package com.yukthitech.autox.ide;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TIdeFileUtils
{
	@Test
	public void testRelativePath()
	{
		File parentFile = new File("c:\\test");

		Assert.assertEquals(IdeFileUtils.getRelativePath(parentFile, new File("c:\\test\\abc\\def.png")), "abc\\def.png");
		
		Assert.assertEquals(IdeFileUtils.getRelativePath(parentFile, new File("c:\\test123\\abc\\def.png")), null);
		Assert.assertEquals(IdeFileUtils.getRelativePath(parentFile, new File("c:\\xyz\\abc\\def.png")), null);
		
		Assert.assertEquals(IdeFileUtils.getRelativePath(parentFile, new File("c:\\test\\")), "");
		Assert.assertEquals(IdeFileUtils.getRelativePath(parentFile, new File("c:\\test")), "");
		
		Assert.assertEquals(IdeFileUtils.getRelativePath(new File("c:\\test\\t.png"), new File("c:\\test\\t.png")), "");
	}
}
