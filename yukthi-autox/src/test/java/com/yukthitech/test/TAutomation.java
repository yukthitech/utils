package com.yukthitech.test;

import java.io.File;
import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.AutomationLauncher;
import com.yukthitech.autox.test.FullExecutionDetails;
import com.yukthitech.autox.test.TestStatus;

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
				"-rf", "./output", 
				"-prop", "./src/test/resources/app.properties", 
				//"-ts", "rest-test-suites"
				//"-tc", "testGroupRecursion"
				//"-list", "com.yukthitech.autox.event.DemoModeAutomationListener"
			});
		
		ExecutionResult exeResult = objectMapper.readValue(new File("./output/test-results.json"), ExecutionResult.class);
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
				"-rf", "./output", 
				"-prop", "./src/test/resources/app.properties", 
				//"-ts", "rest-test-suites"
				//"-tc", "testGroupRecursion"
				//"-list", "com.yukthitech.autox.event.DemoModeAutomationListener"
			});
		
		FullExecutionDetails exeResult = objectMapper.readValue(new File("./output/test-results.json"), FullExecutionDetails.class);
		
		Assert.assertEquals(exeResult.getTestSuiteCount(), 4, "Found one more test suites.");
		Assert.assertEquals(exeResult.getTestCaseCount(), 9, "Found one more test cases.");
		Assert.assertEquals(exeResult.getTestCaseErroredCount(), 2, "Found one more test cases errored.");
		Assert.assertEquals(exeResult.getTestCaseFailureCount(), 4, "Found one more test cases failed.");
		Assert.assertEquals(exeResult.getTestCaseSkippedCount(), 1, "Found one more test cases skipped.");
		
		TestUtils.validateTestCase("basicAssertFailure", exeResult, TestStatus.FAILED, 
				Arrays.asList("[TC: basicAssertFailure](neg-lang-test-suite.xml:12)"), 
				null);

		TestUtils.validateTestCase("errorInLoop", exeResult, TestStatus.FAILED, 
				Arrays.asList("[TC: errorInLoop](neg-lang-test-suite.xml:25)"), 
				null);

		TestUtils.validateTestCase("deepFail", exeResult, TestStatus.FAILED, 
				Arrays.asList(
					Arrays.asList(
						"[TS: negative-lang-suites].level2(neg-lang-test-suite.xml:37)",
						"[TS: negative-lang-suites].level1(neg-lang-test-suite.xml:42)",
						"[TC: deepFail](neg-lang-test-suite.xml:50)"
					)
				), 
				null);

		TestUtils.validateTestCase("tcSetupFail", exeResult, TestStatus.ERRORED, 
				Arrays.asList(
					Arrays.asList(
						"[setup]",
						"TC [Name: tcSetupFail, Location: neg-lang-test-suite.xml:53].&lt;setup&gt;(neg-lang-test-suite.xml:59)"
					)
				), 
				null);

		TestUtils.validateTestCase("tcCleanupFail", exeResult, TestStatus.ERRORED, 
				Arrays.asList(
					Arrays.asList(
						"[cleanup]",
						"TC [Name: tcCleanupFail, Location: neg-lang-test-suite.xml:66].&lt;cleanup&gt;(neg-lang-test-suite.xml:74)"
					)
				), 
				Arrays.asList("This is from testcase"));

		/***********************************************/
		// Skip Test case validation
		/***********************************************/
		TestUtils.validateTestCase("skip_success", exeResult, TestStatus.SUCCESSFUL, 
				null, 
				Arrays.asList("Mssg from skip_success"));

		TestUtils.validateTestCase("skip_fail", exeResult, TestStatus.FAILED, 
				Arrays.asList("[TC: skip_fail](skip-test-suite.xml:20)"), 
				null);

		TestUtils.validateTestCase("skip_skip", exeResult, TestStatus.SKIPPED, 
				null, 
				null);

		/***********************************************/
		// Test suite setup and cleanup error validation
		/***********************************************/
		TestUtils.validateLogFile(new File("./output/logs/test-suite-setup-err-setup_log.json"), 
				"_testSuiteSetup",
				Arrays.asList("[TS: test-suite-setup-err].&lt;setup&gt;(test-suite-setup-err.xml:8)"), 
				null);

		TestUtils.validateLogFile(new File("./output/logs/test-suite-cleanup-err-setup_log.json"), 
				"_testSuiteSetup",
				null, 
				Arrays.asList("This is from setup"));

		TestUtils.validateTestCase("tsCleanupErr_test", exeResult, TestStatus.SUCCESSFUL, 
				null, 
				Arrays.asList("From testcase"));
		
		TestUtils.validateLogFile(new File("./output/logs/test-suite-cleanup-err-cleanup_log.json"), 
				"_testSuiteCleanup",
				Arrays.asList("[TS: test-suite-cleanup-err].&lt;cleanup&gt;(test-suite-cleanup-err.xml:20)"), 
				null);
	}

	@Test
	public void testGlobalSetupError() throws Exception
	{
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml",
				"-tsf", "./src/test/resources/global-setup-err",
				"-rf", "./output", 
				"-prop", "./src/test/resources/app.properties", 
				//"-ts", "rest-test-suites"
				//"-tc", "testGroupRecursion"
				//"-list", "com.yukthitech.autox.event.DemoModeAutomationListener"
			});
		
		FullExecutionDetails exeResult = objectMapper.readValue(new File("./output/test-results.json"), FullExecutionDetails.class);
		
		Assert.assertEquals(exeResult.getTestSuiteCount(), 0, "Found one more test suites.");
		Assert.assertEquals(exeResult.getTestCaseCount(), 0, "Found one more test cases.");
		
		TestUtils.validateLogFile(new File("./output/logs/_global-setup_log.json"), 
				"_globalSetup",
				Arrays.asList("&lt;global&gt;.&lt;setup&gt;(common.xml:4)"), 
				null);
	}


	@Test
	public void testGlobalCleanupError() throws Exception
	{
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml",
				"-tsf", "./src/test/resources/global-cleanup-err",
				"-rf", "./output", 
				"-prop", "./src/test/resources/app.properties", 
				//"-ts", "rest-test-suites"
				//"-tc", "testGroupRecursion"
				//"-list", "com.yukthitech.autox.event.DemoModeAutomationListener"
			});
		
		FullExecutionDetails exeResult = objectMapper.readValue(new File("./output/test-results.json"), FullExecutionDetails.class);
		
		Assert.assertEquals(exeResult.getTestSuiteCount(), 1, "Found one more test suites.");
		Assert.assertEquals(exeResult.getTestCaseCount(), 1, "Found one more test cases.");
		
		TestUtils.validateLogFile(new File("./output/logs/_global-setup_log.json"), 
				"_globalSetup",
				null,
				Arrays.asList("Message from global setup"));

		TestUtils.validateTestCase("test", exeResult, TestStatus.SUCCESSFUL, 
				null, 
				Arrays.asList("Message from testcase"));

		TestUtils.validateLogFile(new File("./output/logs/_global-cleanup_log.json"), 
				"_globalCleanup",
				Arrays.asList("&lt;global&gt;.&lt;cleanup&gt;(common.xml:8)"),
				null);
	}
}
