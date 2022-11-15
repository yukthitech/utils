package com.yukthitech.test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.exec.report.ExecutionLogData;
import com.yukthitech.autox.exec.report.ExecutionStatusReport;
import com.yukthitech.autox.exec.report.FinalReport;
import com.yukthitech.autox.exec.report.FinalReport.TestSuiteResult;
import com.yukthitech.autox.exec.report.LogLevel;
import com.yukthitech.autox.test.TestStatus;
import com.yukthitech.test.beans.TestLogData;

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
	
	public static void validateLogFile(File logFile, String name, List<Object> expectedErrorMssgs, List<Object> expectedMssgs) throws Exception
	{
		String fileContent = FileUtils.readFileToString(logFile, Charset.defaultCharset());
		fileContent += "var logDataJson = JSON.stringify({header: logs[0], footer: logs[logs.length-1], messages: logs.slice(1, logs.length-1)});";
	
		ScriptEngine JS_ENGINE = new ScriptEngineManager().getEngineByName("nashorn");
		JS_ENGINE.eval(fileContent);
		String resJson = (String) JS_ENGINE.get("logDataJson");
		
		TestLogData testLogData = objectMapper.readValue(resJson, TestLogData.class);
		
		List<Object> foundErrMssgs = new ArrayList<Object>();
		List<Object> foundMssgs = new ArrayList<Object>();
		
		for(ExecutionLogData.Message mssg : testLogData.getMessages())
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
	
	public static void validateTestCase(String name, FinalReport details, TestStatus expectedStatus, 
			List<Object> expectedErrorMssgs, List<Object> expectedMssgs, String outputFolder) throws Exception
	{
		ExecutionStatusReport testCaseResult = null;
		TestSuiteResult testSuiteResult = null;
		
		OUTER: for(TestSuiteResult testSuite : details.getTestSuiteResults())
		{
			for(ExecutionStatusReport tsRes : testSuite.getTestCaseResults())
			{
				if(name.equals(tsRes.getName()))
				{
					testSuiteResult = testSuite;
					testCaseResult = tsRes;
					break OUTER;
				}
			}
		}
		
		Assert.assertEquals(testCaseResult.getMainExecutionDetails().getStatus(), expectedStatus);
		
		if(expectedErrorMssgs == null && expectedMssgs == null)
		{
			return;
		}
		
		String filePath = String.format("./output/%s/logs/tc_%s_%s.js", outputFolder, testSuiteResult.getReport().getName(), testCaseResult.getName());
		validateLogFile(new File(filePath), 
				name, expectedErrorMssgs, expectedMssgs);
	}
}
