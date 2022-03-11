package com.yukthitech.test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.AutomationLauncher;

/**
 * Ensures basic flows are working properly and logs are coming
 * in right places.
 * 
 * @author akranthikiran
 */
public class TSuccessFlows
{
	private List<String> toList(String str)
	{
		String arr[] = str.trim().split("\\s*\\,\\s*");
		return Arrays.asList(arr);
	}
	
	private void checkForLogs(String file, String... expectedLogs) throws Exception
	{
		File parentFolder = new File("./flows-output/success-flow/logs");
		
		String content = FileUtils.readFileToString(new File(parentFolder, file), Charset.defaultCharset());
		
		for(String str : expectedLogs)
		{
			Assert.assertTrue(content.contains(str), String.format("File '%s' does not contain log: %s", file, str));
		}
	}
	
	@SuppressWarnings("unchecked")
	private void validSimpleSuite() throws Exception
	{
		AutomationContext context = AutomationContext.getInstance();
		
		//Validate test suite 1
		List<String> testSuite1Flow = (List<String>) context.getAttribute("testSuite1Flow");
		System.out.println("Test Suite1 Flow: \n" + testSuite1Flow);
		
		Assert.assertEquals(testSuite1Flow, 
				toList("globalSetup, testSuiteSetup, "
						+ "beforeTestCase, testCase1Setup, testCase1, testCase1Cleanup, afterTestCase, "
						+ "beforeTestCase, testCase2Setup, testCase2, testCase2Cleanup, afterTestCase, "
						+ "testSuiteCleanup, globalCleanup")
		);

		checkForLogs("test-suite-1-setup_log.json", "<b>[setup]</b> Setup of test-suite-1");
		checkForLogs("test-suite-1-cleanup_log.json", "<b>[cleanup]</b> Cleanup of test-suite-1");
		
		checkForLogs("test-suite-1_testCase1_log.json", 
				"<b>[prechild]</b> before test-case of test-suite-1",
				"<b>[setup]</b> Setup of Test case 1",
				"Test case 1 - 1",
				"Function function1",
				"Test case 1 - 2",
				"<b>[cleanup]</b> Cleanup of Test case 1",
				"<b>[postchild]</b> after test-case of test-suite-1"
		);

		checkForLogs("test-suite-1_testCase2_log.json", 
				"<b>[prechild]</b> before test-case of test-suite-1",
				"<b>[setup]</b> Setup of Test case 2",
				"Test case 2",
				"<b>[cleanup]</b> Cleanup of Test case 2",
				"<b>[postchild]</b> after test-case of test-suite-1"
		);
	}

	@SuppressWarnings("unchecked")
	private void validateDataproviderSuite() throws Exception
	{
		AutomationContext context = AutomationContext.getInstance();
		
		List<String> testSuite2Flow = (List<String>) context.getAttribute("testSuite2Flow");
		System.out.println("Test Suite2 Flow: \n" + testSuite2Flow);
		
		Assert.assertEquals(testSuite2Flow, 
				toList("globalSetup, testSuiteSetup, "
							+ "beforeTestCase, testCaseSetup, dataSetup, "
								+ "testCase-abc, testCase-def, testCase-ghi, "
							+ "dataCleanup, testCaseCleanup, afterTestCase, "
						+ "testSuiteCleanup, globalCleanup")
		);
		
		checkForLogs("_global-setup_log.json", "<b>[setup]</b> Global setup");
		checkForLogs("_global-cleanup_log.json", "<b>[cleanup]</b> Global cleanup");
		checkForLogs("test-suite-2-setup_log.json", "<b>[setup]</b> Setup of test-suite-2");
		checkForLogs("test-suite-2-cleanup_log.json", "<b>[cleanup]</b> Cleanup of test-suite-2");

		checkForLogs("test-suite-2_testCase21_log.json", 
				"<b>[prechild]</b> before test-case of test-suite-1",
				"<b>[setup]</b> testCase21 - setup",
				"<b>[dataSetup]</b> testCase21 - data-setup",
				"<b>[dataCleanup]</b> testCase21 - data-cleanup",
				"<b>[cleanup]</b> testCase21 - cleanup",
				"<b>[postchild]</b> after test-case of test-suite-1"
		);
		
		checkForLogs("test-suite-2_testCase21_1_log.json", "testCase21 - abc");
		checkForLogs("test-suite-2_testCase21_2_log.json", "testCase21 - def");
		checkForLogs("test-suite-2_testCase21_3_log.json", "testCase21 - ghi");
	}

	@SuppressWarnings("unchecked")
	private void validateDataproviderWithoutSetup() throws Exception
	{
		AutomationContext context = AutomationContext.getInstance();
		
		List<String> testSuite3Flow = (List<String>) context.getAttribute("testSuite3Flow");
		System.out.println("Test Suite3 Flow: \n" + testSuite3Flow);
		
		Assert.assertEquals(testSuite3Flow, 
				toList("globalSetup, "
							+ "testCase-abc, testCase-def, testCase-ghi, "
						+ "globalCleanup")
		);
		
		checkForLogs("test-suite-3_testCase31_4_log.json", "testCase31 - abc");
		checkForLogs("test-suite-3_testCase31_5_log.json", "testCase31 - def");
		checkForLogs("test-suite-3_testCase31_6_log.json", "testCase31 - ghi");
	}

	@Test
	public void testSuccessFlow() throws Exception
	{
		AutomationLauncher.systemExitEnabled = false;
		
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml", 
				"-rf", "./flows-output/success-flow", 
				"-prop", "./src/test/resources/app.properties",
				"-tsf", "./src/test/resources/test-suite-flows/success-flow",
				//"-old", "true"
			});
		
		validSimpleSuite();
		validateDataproviderSuite();
		validateDataproviderWithoutSetup();
	}
}
