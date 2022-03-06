package com.yukthitech.autox.test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.utils.CommonUtils;

public class TestTestSuite
{
	private TestSuite buildTestSuite(Map<String, String> depTree)
	{
		TestSuite ts = new TestSuite();
		
		for(String name : depTree.keySet())
		{
			TestCase tc = new TestCase(name);
			tc.setDependencies(depTree.get(name));
			
			ts.addTestCase(tc);
		}
		
		return ts;
	}
	
	@Test
	public void testOrdering_noDep()
	{
		TestSuite testSuite = buildTestSuite(CommonUtils.toMap(
				"a", null, 
				"b", null, 
				"c", null));

		List<TestCase> tcLst = testSuite.fetchOrderedTestCases();
		String order = tcLst.stream().map(tc -> tc.getName()).collect(Collectors.joining(", "));
		
		Assert.assertEquals(order, "a, b, c");
	}

	@Test
	public void testOrdering_withBasicDep()
	{
		TestSuite testSuite = buildTestSuite(CommonUtils.toMap(
				"a", null, 
				"b", "c, d", 
				"c", null,
				"d", null
		));

		List<TestCase> tcLst = testSuite.fetchOrderedTestCases();
		String order = tcLst.stream().map(tc -> tc.getName()).collect(Collectors.joining(", "));
		
		Assert.assertEquals(order, "a, c, d, b");
	}

	@Test
	public void testOrdering_withDeepDep()
	{
		TestSuite testSuite = buildTestSuite(CommonUtils.toMap(
				"a", null, 
				"b", "c", 
				"c", "d",
				"d", null
		));

		List<TestCase> tcLst = testSuite.fetchOrderedTestCases();
		String order = tcLst.stream().map(tc -> tc.getName()).collect(Collectors.joining(", "));
		
		Assert.assertEquals(order, "a, d, c, b");
	}

	@Test
	public void testOrdering_circularDep()
	{
		TestSuite testSuite = buildTestSuite(CommonUtils.toMap(
				"a", null, 
				"b", "c", 
				"c", "d",
				"d", "a, b"
		));

		try
		{
			testSuite.fetchOrderedTestCases();
			Assert.fail("No exception is thrown");
		}catch(Exception ex)
		{
			Assert.assertEquals(ex.getMessage(), "For testcase 'b' ciruclar dependency occurred at path: b => c => d => b");
		}
	}
}
