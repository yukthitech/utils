package com.yukthitech.autox.ide.xmlfile;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TPatternScanner
{
	@Test
	public void testScanner()
	{
		String src = "one  1   someva-123\n"
				+ "two     2    someva-132";
		
		PatternScanner patternScanner = new PatternScanner(src);
		
		Assert.assertEquals(patternScanner.next("\\w+"), "one");
		patternScanner.skip("\\s*");
		Assert.assertEquals(patternScanner.next("\\d+"), "1");
		patternScanner.skip("\\s*");
		
		Assert.assertEquals(patternScanner.next("(\\w+)\\-(\\d+)"), "someva-123");
		PatternScanner.ScannerMatch matcher = patternScanner.getLastMatch();
		
		Assert.assertEquals(matcher.group(1), "someva");
		Assert.assertEquals(matcher.group(2), "123");
		
		patternScanner.skip("\\s*");
		Assert.assertEquals(patternScanner.next("\\w+"), "two");
	}
	
}
