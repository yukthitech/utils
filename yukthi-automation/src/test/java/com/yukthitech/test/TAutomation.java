package com.yukthitech.test;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.yukthitech.automation.AutomationLauncher;

public class TAutomation
{
	@BeforeClass
	public void setup() throws Exception
	{
		TestServer.start(null);
	}
	
	@Test
	public void startAutomation() throws Exception
	{
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml", "./output", "-ts", "jobj-test-suites"});
		System.out.println("From test case printing this...");
	}
}
