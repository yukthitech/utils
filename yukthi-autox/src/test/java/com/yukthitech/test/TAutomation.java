package com.yukthitech.test;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
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
				//"-ts", "lang-test-suites"
				//"-tc", "uiGetElements"
				//"-list", "com.yukthitech.autox.event.DemoModeAutomationListener"
			});
		
		ExecutionResult exeResult = objectMapper.readValue(new File("./output/test-results.json"), ExecutionResult.class);
		Assert.assertEquals(exeResult.getTestCaseErroredCount(), 0, "Found one more test cases errored.");
		Assert.assertEquals(exeResult.getTestCaseFailureCount(), 0, "Found one more test cases failed.");
		Assert.assertEquals(exeResult.getTestCaseSkippedCount(), 0, "Found one more test cases skipped.");
	}
}
