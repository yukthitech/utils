package com.yukthitech.test;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.AutomationLauncher;

/**
 * Base classes for test classes.
 * @author akranthikiran
 */
public class BaseTestCases
{
	protected ObjectMapper objectMapper = new ObjectMapper(); 
	
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
	
	@BeforeMethod
	public void reset() throws Exception
	{
		AutomationLauncher.resetState();
	}
}
