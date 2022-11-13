package com.yukthitech.test;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtils
{
	private static ObjectMapper objectMapper = new ObjectMapper(); 
	
	@SuppressWarnings("unchecked")
	private static void checkForMessages(String actualMssg, List<Object> expectedMssgs, List<Object> foundMssgs)
	{
		if(expectedMssgs == null)
		{
			return;
		}
		
		MSG_LOOP: for(Object mssg : expectedMssgs)
		{
			if(foundMssgs.contains(mssg))
			{
				continue;
			}
			
			List<String> mssgLst = (mssg instanceof String) ? Arrays.asList(mssg.toString()) : (List<String>) mssg;
			
			for(String mssgStr : mssgLst)
			{
				if(!actualMssg.contains(mssgStr))
				{
					continue MSG_LOOP;
				}
			}
			
			foundMssgs.add(mssg);
		}
	}
	/*
	public static void validateLogFile(File logFile, String name, List<Object> expectedErrorMssgs, List<Object> expectedMssgs) throws Exception
	{
		ExecutionLogData logData = objectMapper.readValue(logFile, ExecutionLogData.class);
		List<Object> foundErrMssgs = new ArrayList<Object>();
		List<Object> foundMssgs = new ArrayList<Object>();
		
		for(ExecutionLogData.Message mssg : logData.getMessages())
		{
			if(mssg.getLogLevel() == LogLevel.ERROR)
			{
				checkForMessages(mssg.getMessage(), expectedErrorMssgs, foundErrMssgs);
				continue;
			}
			
			checkForMessages(mssg.getMessage(), expectedMssgs, foundMssgs);
		}
		
		if(expectedErrorMssgs != null && expectedErrorMssgs.size() != foundErrMssgs.size())
		{
			expectedErrorMssgs = new ArrayList<>(expectedErrorMssgs);
			
			expectedErrorMssgs.removeAll(foundErrMssgs);
			Assert.fail(String.format("For test-case '%s' below expected ERROR mssgs are not found: \n\t%s\n\n"
					+ "Mssgs Found:\n", name,
					expectedErrorMssgs.stream().map(obj -> obj.toString()).collect(Collectors.joining("\n\t"))
					));
		}

		if(expectedMssgs != null && expectedMssgs.size() != foundMssgs.size())
		{
			expectedMssgs = new ArrayList<>(expectedMssgs);
			
			expectedMssgs.removeAll(foundMssgs);
			Assert.fail(String.format("For test-case '%s' below expected mssgs are not found: \n\t%s", name,
					expectedMssgs.stream().map(obj -> obj.toString()).collect(Collectors.joining("\n\t"))
					));
		}
	}
	
	public static void validateTestCase(String name, FullExecutionDetails details, TestStatus expectedStatus, 
			List<Object> expectedErrorMssgs, List<Object> expectedMssgs) throws Exception
	{
		TestCaseResult testCaseResult = null;
		TestSuiteResults testSuiteResult = null;
		
		for(TestSuiteResults testSuite : details.getTestSuiteResults())
		{
			for(TestCaseResult tsRes : testSuite.getTestCaseResults())
			{
				if(name.equals(tsRes.getTestCaseName()))
				{
					testSuiteResult = testSuite;
					testCaseResult = tsRes;
					break;
				}
			}
		}
		
		Assert.assertEquals(testCaseResult.getStatus(), expectedStatus);
		
		if(expectedErrorMssgs == null || expectedMssgs == null)
		{
			return;
		}
		
		validateLogFile(new File("./output/logs/" + testSuiteResult.getSuiteName() + "_" + testCaseResult.getTestCaseName() + "_log.json"), 
				name, expectedErrorMssgs, expectedMssgs);
	}
*/
}
