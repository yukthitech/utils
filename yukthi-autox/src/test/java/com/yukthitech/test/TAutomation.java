package com.yukthitech.test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.AutomationLauncher;

public class TAutomation
{
	private ObjectMapper objectMapper = new ObjectMapper(); 
	
	@BeforeClass
	public void setup() throws Exception
	{
		TestServer.start(null);
	}
	
	@Test
	public void startAutomation() throws Exception
	{
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml", 
				"-rf", "./output", 
				"-prop", "./src/test/resources/app.properties", 
				//"-ts", "rest-test-suites"
				"-tc", "jsonAndJel"
				//"-list", "com.yukthitech.autox.event.DemoModeAutomationListener"
			});
		
		ExecutionResult exeResult = objectMapper.readValue(new File("./output/test-results.json"), ExecutionResult.class);
		Assert.assertEquals(exeResult.getTestCaseErroredCount(), 0, "Found one more test cases errored.");
		Assert.assertEquals(exeResult.getTestCaseFailureCount(), 0, "Found one more test cases failed.");
		Assert.assertEquals(exeResult.getTestCaseSkippedCount(), 0, "Found one more test cases skipped.");
	}

	@SuppressWarnings("unchecked")
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
		
		AutomationContext context = AutomationContext.getInstance();
		List<String> testSuite1Flow = (List<String>) context.getAttribute("testSuite1Flow");
		System.out.println(testSuite1Flow);
		
		Assert.assertEquals(testSuite1Flow, 
				Arrays.asList(
						"globalSetup", 
						"testSuiteSetup",
						
						"beforeTestCase", 
						"testCase1Setup", 
						"testCase1",
						"testCase1Cleanup", 
						"afterTestCase",
						
						"beforeTestCase", 
						"testCase2Setup", 
						"testCase2", 
						"testCase2Cleanup", 
						"afterTestCase",
						
						"testSuiteCleanup",
						"globalCleanup"
						));
		
		ExecutionResult exeResult = objectMapper.readValue(new File("./flows-output/success-flow/test-results.json"), ExecutionResult.class);
	}
}
