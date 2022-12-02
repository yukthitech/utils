package com.yukthitech.test;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.autox.AutomationLauncher;
import com.yukthitech.autox.debug.client.DebugClient;
import com.yukthitech.autox.debug.client.IDebugClientHandler;
import com.yukthitech.autox.debug.common.ClientMssgDebugOp;
import com.yukthitech.autox.debug.common.ClientMssgDebuggerInit;
import com.yukthitech.autox.debug.common.DebugOp;
import com.yukthitech.autox.debug.common.DebugPoint;
import com.yukthitech.autox.debug.common.ServerMssgExecutionPaused;
import com.yukthitech.autox.debug.common.ServerMssgExecutionReleased;
import com.yukthitech.autox.exec.report.FinalReport;
import com.yukthitech.utils.ObjectWrapper;

public class TDebugFlows extends BaseTestCases
{
	private static Logger logger = LogManager.getLogger(TDebugFlows.class);
	
	private static class DebugClientHandler implements IDebugClientHandler
	{
		private DebugClient debugClient;
		
		private List<Integer> pausedLocations = new ArrayList<>();
		
		private String expectedFile;
		
		private DebugOp debugOp;
		
		public DebugClientHandler(DebugClient debugClient, String expectedFile, DebugOp debugOp)
		{
			this.debugClient = debugClient;
			this.expectedFile = expectedFile;
			this.debugOp = debugOp;
		}

		@Override
		public void processData(Serializable data)
		{
			if(data instanceof ServerMssgExecutionPaused)
			{
				ServerMssgExecutionPaused mssg = (ServerMssgExecutionPaused) data;
				
				Assert.assertEquals(mssg.getDebugFilePath(), expectedFile);
				pausedLocations.add(mssg.getLineNumber());
				
				logger.debug("Debug with execution-id {} is pasused at {}:{}", 
						mssg.getExecutionId(), mssg.getDebugFilePath(), mssg.getLineNumber());
				
				debugClient.sendDataToServer(new ClientMssgDebugOp(mssg.getExecutionId(), debugOp));
			}
			else if(data instanceof ServerMssgExecutionReleased)
			{
				ServerMssgExecutionReleased mssg = (ServerMssgExecutionReleased) data;
				logger.debug("Debug with execution-id {} is released", mssg.getExecutionId());
			}
		}
	}
	
	public void testDebugFlow(DebugOp debugOp, List<Integer> debugPointLines, List<Integer> expectedPauses) throws Exception
	{
		ObjectWrapper<DebugClientHandler> clientHandler = new ObjectWrapper<>();
		
		Thread clientThread = new Thread() 
		{
			public void run()
			{
				try
				{
					String debugFile = new File("./src/test/resources/new-test-suites/debug-flow/debug-flow-suite.xml").getCanonicalPath();
					
					List<DebugPoint> points = new ArrayList<>();
					
					for(int line : debugPointLines)
					{
						points.add(new DebugPoint(debugFile, line, null));
					}
					
					ClientMssgDebuggerInit initDebugPoints = new ClientMssgDebuggerInit(points);
					
					DebugClient debugClient = DebugClient.newClient("localhost", 9876, initDebugPoints);
					DebugClientHandler handler = new DebugClientHandler(debugClient, debugFile, debugOp);
					
					clientHandler.setValue(handler);
					
					debugClient
						.addDataHandler(handler)
						.start();
				}catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		};
		
		clientThread.start();
		
		AutomationLauncher.main(new String[] {"./src/test/resources/app-configuration.xml",
				"-tsf", "./src/test/resources/new-test-suites/debug-flow",
				"-rf", "./output/debug-flow", 
				"-prop", "./src/test/resources/app.properties", 
				"--debug-port", "9876",
				"--report-opening-disabled", "true",
				//"-ts", "data-provider-err"
				//"-tc", "screenShotInCleanupErr"
				//"-list", "com.yukthitech.autox.event.DemoModeAutomationListener"
			});
		
		
		System.out.println("Halt points: " + clientHandler.getValue().pausedLocations);
		
		Assert.assertEquals(clientHandler.getValue().pausedLocations, expectedPauses);
		
		FinalReport exeResult = objectMapper.readValue(new File("./output/debug-flow/test-results.json"), FinalReport.class);
		
		Assert.assertEquals(exeResult.getTestSuiteCount(), 1, "Found one more test suites.");
		Assert.assertEquals(exeResult.getTestCaseCount(), 1, "Found one more test cases.");
		Assert.assertEquals(exeResult.getTestCaseSuccessCount(), 1, "Found one more test cases errored.");
	}
	
	@Test
	public void testStepIntoFlow() throws Exception
	{
		testDebugFlow(DebugOp.STEP_INTO, Arrays.asList(8), Arrays.asList(
				//setup
				8, 9,
				
				//testcase
				29, 30, 32,
					//function2
					18, 19, 21,
					//function 1
					13, 14,
					
				//cleanup	
				36, 37
			));
	}

	@Test
	public void testStepOverFlow() throws Exception
	{
		testDebugFlow(DebugOp.STEP_OVER, Arrays.asList(8), Arrays.asList(
				//setup
				8, 9,
				
				//testcase
				29, 30, 32,
					
				//cleanup	
				36, 37
			));
	}
	
	@Test
	public void testStepReturnFlow() throws Exception
	{
		testDebugFlow(DebugOp.STEP_RETURN, Arrays.asList(8, 13, 36), Arrays.asList(8, 13, 36));
	}

}
