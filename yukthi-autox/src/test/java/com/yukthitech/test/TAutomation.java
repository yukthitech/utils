package com.yukthitech.test;

import java.io.File;
import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.AutomationLauncher;
import com.yukthitech.autox.exec.report.FinalReport;
import com.yukthitech.autox.test.TestStatus;
import com.yukthitech.test.beans.ExecutionResult;

public class TAutomation
{
	private ObjectMapper objectMapper = new ObjectMapper(); 
	
	@BeforeClass
	public void setup() throws Exception
	{
		AutomationLauncher.systemExitEnabled = false;
		TestServer.start(null);
	}
	
	@AfterClass
	public void close() throws Exception
	{
		TestServer.stop();
	}
	
	@Test
	public void testSuccessCases() throws Exception
	{
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml", 
				"-rf", "./output/success", 
				"-prop", "./src/test/resources/app.properties", 
				//"-ts", "invoke_Method_With_Data_Provder"
				//"-tc", "testExternalIfElse"
				//"-list", "com.yukthitech.autox.event.DemoModeAutomationListener"
			});
		
		ExecutionResult exeResult = objectMapper.readValue(new File("./output/success/test-results.json"), ExecutionResult.class);
		Assert.assertEquals(exeResult.getTestCaseErroredCount(), 0, "Found one more test cases errored.");
		Assert.assertEquals(exeResult.getTestCaseFailureCount(), 0, "Found one more test cases failed.");
		Assert.assertEquals(exeResult.getTestCaseSkippedCount(), 0, "Found one more test cases skipped.");
	}

	/**
	 * Ensures failures are happening at right places and with right stack trace.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testNegativeCases() throws Exception
	{
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml",
				"-tsf", "./src/test/resources/neg-test-suites",
				"-rf", "./output/negCases", 
				"-prop", "./src/test/resources/app.properties", 
				//"-ts", "rest-test-suites"
				//"-tc", "screenShotInCleanupErr"
				//"-list", "com.yukthitech.autox.event.DemoModeAutomationListener"
			});
		
		FinalReport exeResult = objectMapper.readValue(new File("./output/negCases/test-results.json"), FinalReport.class);
		
		Assert.assertEquals(exeResult.getTestSuiteCount(), 7, "Found one more test suites.");
		Assert.assertEquals(exeResult.getTestCaseCount(), 11, "Found one more test cases.");
		Assert.assertEquals(exeResult.getTestCaseErroredCount(), 3, "Found one more test cases errored.");
		Assert.assertEquals(exeResult.getTestCaseFailureCount(), 4, "Found one more test cases failed.");
		Assert.assertEquals(exeResult.getTestCaseSkippedCount(), 1, "Found one more test cases skipped.");
		
		TestUtils.validateTestCase("basicAssertFailure", exeResult, TestStatus.FAILED, 
				Arrays.asList("[TC: basicAssertFailure](neg-lang-test-suite.xml:12)"), 
				null, null, "negCases");

		TestUtils.validateTestCase("errorInLoop", exeResult, TestStatus.FAILED, 
				Arrays.asList("[TC: errorInLoop](neg-lang-test-suite.xml:25)"), 
				null, null, "negCases");

		TestUtils.validateTestCase("deepFail", exeResult, TestStatus.FAILED, 
				Arrays.asList(
					Arrays.asList(
						"[TS: negative-lang-suites].level2(neg-lang-test-suite.xml:37)",
						"[TS: negative-lang-suites].level1(neg-lang-test-suite.xml:42)",
						"[TC: deepFail](neg-lang-test-suite.xml:50)"
					)
				), 
				null, null, "negCases");

		TestUtils.validateTestCase("tcSetupFail", exeResult, TestStatus.ERRORED, 
				Arrays.asList(
					Arrays.asList(
						"[Setup]",
						"[TC: tcSetupFail](neg-lang-test-suite.xml:59)"
					)
				), 
				null, null, "negCases");

		TestUtils.validateTestCase("tcCleanupFail", exeResult, TestStatus.ERRORED, 
				Arrays.asList(
					Arrays.asList(
						"[Cleanup]",
						"[TC: tcCleanupFail](neg-lang-test-suite.xml:74)"
					)
				), 
				Arrays.asList("This is from testcase"), 
				null, "negCases");

		/***********************************************/
		// UI Error Test case validation
		/***********************************************/
		TestUtils.validateTestCase("screenShotOnError", exeResult, TestStatus.ERRORED, 
				Arrays.asList(
					"Failed to find element with locator: [Locator: id: invalidId]",
					"Screen shot during error"
				), 
				null, Arrays.asList("error-screenshot"), "negCases");

		TestUtils.validateLogFile(new File("./output/negCases/logs/ts_neg-ui-test-suites-setupErr-setup.js"), 
				"test-suite-setup-err",
				Arrays.asList("[TS: neg-ui-test-suites-setupErr](neg-ui-test-suite.xml:22)"), 
				null,
				Arrays.asList("error-screenshot"));

		TestUtils.validateLogFile(new File("./output/negCases/logs/ts_neg-ui-test-suites-cleanupErr-cleanup.js"), 
				"test-suite-cleanup-err",
				Arrays.asList("[TS: neg-ui-test-suites-cleanupErr](neg-ui-test-suite.xml:48)"), 
				null,
				Arrays.asList("error-screenshot"));
		/***********************************************/
		// Skip Test case validation
		/***********************************************/
		TestUtils.validateTestCase("skip_success", exeResult, TestStatus.SUCCESSFUL, 
				null, 
				Arrays.asList("Mssg from skip_success"), null, "negCases");

		TestUtils.validateTestCase("skip_fail", exeResult, TestStatus.FAILED, 
				Arrays.asList("[TC: skip_fail](skip-test-suite.xml:20)"), 
				null, null, "negCases");

		TestUtils.validateTestCase("skip_skip", exeResult, TestStatus.SKIPPED, 
				null, 
				null, null, "negCases");

		/***********************************************/
		// Test suite setup and cleanup error validation
		/***********************************************/
		TestUtils.validateLogFile(new File("./output/negCases/logs/ts_test-suite-setup-err-setup.js"), 
				"_testSuiteSetup",
				Arrays.asList("[TS: test-suite-setup-err](test-suite-setup-err.xml:8)"), 
				null, null);

		TestUtils.validateLogFile(new File("./output/negCases/logs/ts_test-suite-cleanup-err-setup.js"), 
				"_testSuiteSetup",
				null, 
				Arrays.asList("This is from setup"), null);

		TestUtils.validateTestCase("tsCleanupErr_test", exeResult, TestStatus.SUCCESSFUL, 
				null, 
				Arrays.asList("From testcase"), null, "negCases");
		
		TestUtils.validateLogFile(new File("./output/negCases/logs/ts_test-suite-cleanup-err-cleanup.js"), 
				"_testSuiteCleanup",
				Arrays.asList("[TS: test-suite-cleanup-err](test-suite-cleanup-err.xml:20)"), 
				null, null);
	}

	@Test
	public void testGlobalSetupError() throws Exception
	{
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml",
				"-tsf", "./src/test/resources/global-setup-err",
				"-rf", "./output/globalSetupErr", 
				"-prop", "./src/test/resources/app.properties", 
				//"-ts", "rest-test-suites"
				//"-tc", "testGroupRecursion"
				//"-list", "com.yukthitech.autox.event.DemoModeAutomationListener"
			});
		
		FinalReport exeResult = objectMapper.readValue(new File("./output/globalSetupErr/test-results.json"), FinalReport.class);
		
		Assert.assertEquals(exeResult.getTestSuiteCount(), 0, "Found one more test suites.");
		Assert.assertEquals(exeResult.getTestCaseCount(), 0, "Found one more test cases.");
		
		TestUtils.validateLogFile(new File("./output/globalSetupErr/logs/_global-setup.js"), 
				"_globalSetup",
				Arrays.asList("&lt;Test-Suite-Group&gt;(common.xml:4)"), 
				null, null);
	}

	@Test
	public void testGlobalCleanupError() throws Exception
	{
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml",
				"-tsf", "./src/test/resources/global-cleanup-err",
				"-rf", "./output/globalCleanupErr", 
				"-prop", "./src/test/resources/app.properties", 
				//"-ts", "rest-test-suites"
				//"-tc", "testGroupRecursion"
				//"-list", "com.yukthitech.autox.event.DemoModeAutomationListener"
			});
		
		FinalReport exeResult = objectMapper.readValue(new File("./output/globalCleanupErr/test-results.json"), FinalReport.class);
		
		Assert.assertEquals(exeResult.getTestSuiteCount(), 1, "Found one more test suites.");
		Assert.assertEquals(exeResult.getTestCaseCount(), 1, "Found one more test cases.");
		
		TestUtils.validateLogFile(new File("./output/globalCleanupErr/logs/_global-setup.js"), 
				"_globalSetup",
				null,
				Arrays.asList("Message from global setup"), null);

		TestUtils.validateTestCase("test", exeResult, TestStatus.SUCCESSFUL, 
				null, 
				Arrays.asList("Message from testcase"), null, "globalCleanupErr");

		TestUtils.validateLogFile(new File("./output/globalCleanupErr/logs/_global-cleanup.js"), 
				"_globalCleanup",
				Arrays.asList("&lt;Test-Suite-Group&gt;(common.xml:8)"),
				null, null);
	}
}
