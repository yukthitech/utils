package com.yukthitech.autox.event;

import com.yukthitech.autox.test.TestStatus;

/**
 * Useful while executing test suites for demos. This listener will display the active test case information.
 * @author akiran
 */
public class DemoModeAutomationListener implements IAutomationListener
{
	private DemoModeMessageDialog demoModeMessageDialog = new DemoModeMessageDialog();

	private void sleep()
	{
		try
		{
			Thread.sleep(3000);
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	@Override
	public void testCaseStarted(AutomationEvent event)
	{
		demoModeMessageDialog.display("Starting test case...", event.getTestSuite(), event.getTestCase(), null);
		sleep();
		
		demoModeMessageDialog.close();
	}

	@Override
	public void testCaseCompleted(AutomationEvent event)
	{
		if(event.getTestCaseResult().getStatus() == TestStatus.SUCCESSFUL)
		{
			demoModeMessageDialog.display("Successfully completed test case...", event.getTestSuite(), event.getTestCase(), true);
		}
		else
		{
			demoModeMessageDialog.display("Test case failed with status: " + event.getTestCaseResult().getStatus(), event.getTestSuite(), event.getTestCase(), false);
		}
		
		sleep();
		demoModeMessageDialog.close();
	}
}
