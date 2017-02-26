package com.yukthitech.test.ui;

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
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml", "./output"});
	}
}
