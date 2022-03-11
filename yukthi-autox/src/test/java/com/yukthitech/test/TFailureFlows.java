package com.yukthitech.test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.AutomationLauncher;
import com.yukthitech.autox.test.FullExecutionDetails;
import com.yukthitech.autox.test.TestCaseResult;
import com.yukthitech.autox.test.TestStatus;
import com.yukthitech.autox.test.TestSuiteResults;

/**
 * Ensures basic flows are working properly and logs are coming
 * in right places.
 * 
 * @author akranthikiran
 */
public class TFailureFlows
{
	private ObjectMapper objectMapper = new ObjectMapper(); 
	
	private List<String> toList(String str)
	{
		String arr[] = str.trim().split("\\s*\\,\\s*");
		return Arrays.asList(arr);
	}
	
	private void checkForLogs(String file, String... expectedLogs) throws Exception
	{
		File parentFolder = new File("./flows-output/failure-flow/logs");
		
		String content = FileUtils.readFileToString(new File(parentFolder, file), Charset.defaultCharset());
		
		for(String str : expectedLogs)
		{
			Assert.assertTrue(content.contains(str), String.format("File '%s' does not contain log: %s", file, str));
		}
	}
	
	@SuppressWarnings("unchecked")
	private void validTestSuiteSetupFail(FullExecutionDetails exeResult) throws Exception
	{
		AutomationContext context = AutomationContext.getInstance();
		
		TestSuiteResults tsResults = exeResult.getTestSuiteResults("testSuiteSetupFail");
		Assert.assertEquals(tsResults.getSuccessCount(), 0);
		Assert.assertEquals(tsResults.getFailureCount(), 0);
		Assert.assertEquals(tsResults.getErrorCount(), 0);
		Assert.assertEquals(tsResults.getSkipCount(), 0);
		Assert.assertEquals(tsResults.getStatusMessage(), "Setup execution failed.");
		
		Assert.assertEquals(tsResults.isSetupSuccessful(), false);
		Assert.assertEquals(tsResults.isCleanupSuccessful(), true);
		
		//Validate test suite 1
		List<String> testSuite1Flow = (List<String>) context.getAttribute("testSuiteSetupFailFlow");
		System.out.println("Test Suite Setup Fail Flow: \n" + testSuite1Flow);
		
		//Note test suite cleanup should not execute, as setup is failed
		Assert.assertEquals(testSuite1Flow, 
				toList("globalSetup, testSuiteSetup, "
						+ " globalCleanup")
		);
		
		checkForLogs("testSuiteSetupFail-setup_log.json", 
				"Test suite setup: testSuiteSetupFail",
				"<b>[setup]</b> An error occurred with message - Failing test suite");
	}

	@SuppressWarnings("unchecked")
	private void validBeforeTestCaseFail(FullExecutionDetails exeResult) throws Exception
	{
		AutomationContext context = AutomationContext.getInstance();
		
		TestSuiteResults tsResults = exeResult.getTestSuiteResults("testSuiteBeforeTcFail");
		Assert.assertEquals(tsResults.getSuccessCount(), 1);
		Assert.assertEquals(tsResults.getFailureCount(), 0);
		Assert.assertEquals(tsResults.getErrorCount(), 1);
		Assert.assertEquals(tsResults.getSkipCount(), 0);
		Assert.assertEquals(tsResults.getStatusMessage(), "Failing as one or more test cases failed");
		
		Assert.assertEquals(tsResults.isSetupSuccessful(), true);
		Assert.assertEquals(tsResults.isCleanupSuccessful(), true);

		TestCaseResult tcResult = tsResults.getTestCaseResult("tc1");
		Assert.assertEquals(tcResult.getMessage(), "Setup execution failed.");

		//Validate test suite 1
		List<String> testSuite1Flow = (List<String>) context.getAttribute("testSuiteBeforeTcFail");
		System.out.println("Test Suite before-test-case Fail Flow: \n" + testSuite1Flow);
		
		Assert.assertEquals(testSuite1Flow, 
				toList("globalSetup, testSuiteSetup, "
						+ "beforeTestCase, "
						+ "beforeTestCase, tc2Setup, tc2, tc2Cleanup, afterTestCase, "
						+ "testSuiteCleanup, globalCleanup")
		);
		
		checkForLogs("testSuiteBeforeTcFail-setup_log.json", "<b>[setup]</b> Test suite setup: testSuiteBeforeTcFail");
		checkForLogs("testSuiteBeforeTcFail-cleanup_log.json", "<b>[cleanup]</b> Cleanup of testSuiteBeforeTcFail");
		
		checkForLogs("testSuiteBeforeTcFail_tc1_log.json", 
				"<b>[prechild]</b> Got flag as: null",
				"<b>[prechild]</b> An error occurred with message - Failing test case"
		);

		checkForLogs("testSuiteBeforeTcFail_tc2_log.json", 
				"<b>[prechild]</b> Got flag as: true",
				"test case- tc2"
		);
	}

	@SuppressWarnings("unchecked")
	private void validTcSetupFail(FullExecutionDetails exeResult) throws Exception
	{
		AutomationContext context = AutomationContext.getInstance();
		
		TestSuiteResults tsResults = exeResult.getTestSuiteResults("tcSetupFail");
		Assert.assertEquals(tsResults.getSuccessCount(), 1);
		Assert.assertEquals(tsResults.getFailureCount(), 0);
		Assert.assertEquals(tsResults.getErrorCount(), 1);
		Assert.assertEquals(tsResults.getSkipCount(), 0);
		Assert.assertEquals(tsResults.getStatusMessage(), "Failing as one or more test cases failed");
		
		Assert.assertEquals(tsResults.isSetupSuccessful(), true);
		Assert.assertEquals(tsResults.isCleanupSuccessful(), true);

		TestCaseResult tcResult = tsResults.getTestCaseResult("tcf1");
		Assert.assertEquals(tcResult.getMessage(), "Setup execution failed.");

		//Validate test suite 1
		List<String> testSuite1Flow = (List<String>) context.getAttribute("tcSetupFail");
		System.out.println("Test Suite tc-setup Fail Flow: \n" + testSuite1Flow);
		
		Assert.assertEquals(testSuite1Flow, 
				toList("globalSetup, testSuiteSetup, "
						+ "beforeTestCase, tc1Setup, "
						+ "beforeTestCase, tc2Setup, tc2, tc2Cleanup, afterTestCase, "
						+ "testSuiteCleanup, globalCleanup")
		);
		
		checkForLogs("tcSetupFail-setup_log.json", "<b>[setup]</b> Test suite setup: tcSetupFail");
		checkForLogs("tcSetupFail-cleanup_log.json", "<b>[cleanup]</b> Cleanup of tcSetupFail");
		
		checkForLogs("tcSetupFail_tcf1_log.json", 
				"<b>[prechild]</b> Before test case: tcSetupFail",
				"<b>[setup]</b> Got flag as: null",
				"<b>[setup]</b> An error occurred with message - Failing test case"
		);

		checkForLogs("tcSetupFail_tcf2_log.json", 
				"<b>[prechild]</b> Before test case: tcSetupFail",
				"test case- tc2"
		);
	}

	@SuppressWarnings("unchecked")
	private void validTcFail(FullExecutionDetails exeResult) throws Exception
	{
		AutomationContext context = AutomationContext.getInstance();
		
		TestSuiteResults tsResults = exeResult.getTestSuiteResults("tcFail");
		Assert.assertEquals(tsResults.getSuccessCount(), 2);
		Assert.assertEquals(tsResults.getFailureCount(), 1);
		Assert.assertEquals(tsResults.getErrorCount(), 2);
		Assert.assertEquals(tsResults.getSkipCount(), 1);
		Assert.assertEquals(tsResults.getStatusMessage(), "Failing as one or more test cases failed");
		
		Assert.assertEquals(tsResults.isSetupSuccessful(), true);
		Assert.assertEquals(tsResults.isCleanupSuccessful(), true);
		
		TestCaseResult tcResult = tsResults.getTestCaseResult("tcf1");
		Assert.assertEquals(tcResult.getMessage(), "Validation assertEquals failed. Validation Details: AssertEqualsStep[actual=1,expected=2]");
		Assert.assertEquals(tcResult.getStatus(), TestStatus.FAILED);

		tcResult = tsResults.getTestCaseResult("tcf3");
		Assert.assertEquals(tcResult.getMessage(), "Skipping as dependency test case was not successul: tcf1");
		Assert.assertEquals(tcResult.getStatus(), TestStatus.SKIPPED);

		tcResult = tsResults.getTestCaseResult("tcf4");
		Assert.assertEquals(tcResult.getStatus(), TestStatus.ERRORED);

		tcResult = tsResults.getTestCaseResult("tcCleanupFail");
		Assert.assertEquals(tcResult.getMessage(), "Cleanup execution failed.");
		Assert.assertEquals(tcResult.getStatus(), TestStatus.ERRORED);

		//Validate test suite 1
		List<String> testSuite1Flow = (List<String>) context.getAttribute("tcFail");
		System.out.println("Test Suite tc-fail Flow: \n" + testSuite1Flow);
		
		Assert.assertEquals(testSuite1Flow, 
				toList("globalSetup, testSuiteSetup, "
						+ "beforeTestCase, tcf1Setup, tcf1Cleanup, afterTestCase, "
						+ "beforeTestCase, tcf2Setup, tcf2, tcf2Cleanup, afterTestCase, "
						+ "beforeTestCase, tcf4Setup, tcf4Cleanup, afterTestCase, "
						+ "beforeTestCase, tcf5Setup, tcf5, tcf5Cleanup, afterTestCase, "
						+ "beforeTestCase, tcCleanupFail, tcCleanupFail, afterTestCase, "
						+ "testSuiteCleanup, globalCleanup")
		);
		
		checkForLogs("tcFail-setup_log.json", "<b>[setup]</b> Test suite setup: tcFail");
		checkForLogs("tcFail-cleanup_log.json", "<b>[cleanup]</b> Cleanup of tcFail");
		
		checkForLogs("tcFail_tcf1_log.json", 
				"<b>[prechild]</b> Before test case: tcFail",
				"Validation assertEquals failed. Validation Details: AssertEqualsStep[actual=1,expected=2]"
		);

		checkForLogs("tcFail_tcf4_log.json", 
				"<b>[prechild]</b> Before test case: tcFail",
				"Throwing error with message: Failing test case"
		);

		checkForLogs("tcFail_tcCleanupFail_log.json", 
				"<b>[cleanup]</b> An error occurred with message - Failing test case"
		);
	}

	@SuppressWarnings("unchecked")
	private void validTsCleanupFail(FullExecutionDetails exeResult) throws Exception
	{
		AutomationContext context = AutomationContext.getInstance();
		
		TestSuiteResults tsResults = exeResult.getTestSuiteResults("tsCleanupFail");
		Assert.assertEquals(tsResults.getSuccessCount(), 1);
		Assert.assertEquals(tsResults.getFailureCount(), 0);
		Assert.assertEquals(tsResults.getErrorCount(), 0);
		Assert.assertEquals(tsResults.getSkipCount(), 0);
		Assert.assertEquals(tsResults.getStatusMessage(), "Cleanup execution failed.");
		
		Assert.assertEquals(tsResults.isSetupSuccessful(), true);
		Assert.assertEquals(tsResults.isCleanupSuccessful(), false);
		
		//Validate test suite 1
		List<String> testSuite1Flow = (List<String>) context.getAttribute("tsCleanupFail");
		System.out.println("Test Suite cleanup Fail Flow: \n" + testSuite1Flow);
		
		//Note test suite cleanup should not execute, as setup is failed
		Assert.assertEquals(testSuite1Flow, 
				toList("globalSetup, testSuiteSetup, "
						+ "beforeTestCase, tcf1Setup, tcf1, tcf1Cleanup, afterTestCase, "
						+ "globalCleanup")
		);
		
		checkForLogs("tsCleanupFail-cleanup_log.json", 
				"<b>[cleanup]</b> An error occurred with message - Failing test suite");
	}

	@SuppressWarnings("unchecked")
	private void validTsDataFail(FullExecutionDetails exeResult) throws Exception
	{
		AutomationContext context = AutomationContext.getInstance();
		
		TestSuiteResults tsResults = exeResult.getTestSuiteResults("testSuiteData");
		Assert.assertEquals(tsResults.getSuccessCount(), 3);
		Assert.assertEquals(tsResults.getFailureCount(), 0);
		Assert.assertEquals(tsResults.getErrorCount(), 2);
		Assert.assertEquals(tsResults.getSkipCount(), 0);
		Assert.assertEquals(tsResults.getStatusMessage(), "Failing as one or more test cases failed");
		
		Assert.assertEquals(tsResults.isSetupSuccessful(), true);
		Assert.assertEquals(tsResults.isCleanupSuccessful(), true);
		
		//Validate test suite 1
		List<String> testSuite1Flow = (List<String>) context.getAttribute("testSuiteData");
		System.out.println("Test Suite data fail flow: \n" + testSuite1Flow);
		
		//Note test suite cleanup should not execute, as setup is failed
		Assert.assertEquals(testSuite1Flow, 
				toList("globalSetup, "
						+ "testCaseSetup-dataSetupFail, "
						+ "testCaseSetup-dataCleanupFail, dataSetup-dataCleanupFail, testCase-abc, testCase-def, testCase-ghi, testCaseCleanup-dataCleanupFail, "
						+ "globalCleanup")
		);
		
		checkForLogs("testSuiteData_dataSetupFail_log.json", 
				"<b>[dataSetup]</b> An error occurred with message - Validation assertEquals failed. Validation Details: AssertEqualsStep[actual=1,expected=2]");

		checkForLogs("testSuiteData_dataCleanupFail_log.json", 
				"<b>[dataCleanup]</b> An error occurred with message - Validation assertEquals failed. Validation Details: AssertEqualsStep[actual=1,expected=2]");
	}

	@Test
	public void testFailureFlows() throws Exception
	{
		AutomationLauncher.systemExitEnabled = false;
		
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml", 
				"-rf", "./flows-output/failure-flow", 
				"-prop", "./src/test/resources/app.properties",
				"-tsf", "./src/test/resources/test-suite-flows/failure-flow",
				//"-old", "true"
			});
		
		FullExecutionDetails exeResult = objectMapper.readValue(new File("./flows-output/failure-flow/test-results.json"), FullExecutionDetails.class);
		
		checkForLogs("_global-setup_log.json", "<b>[setup]</b> Global setup");
		checkForLogs("_global-cleanup_log.json", "<b>[cleanup]</b> Global cleanup");

		validTestSuiteSetupFail(exeResult);
		validBeforeTestCaseFail(exeResult);
		validTcSetupFail(exeResult);
		validTcFail(exeResult);
		validTsCleanupFail(exeResult);
		validTsDataFail(exeResult);
	}
}
