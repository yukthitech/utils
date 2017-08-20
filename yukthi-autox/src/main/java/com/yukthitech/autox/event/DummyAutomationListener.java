package com.yukthitech.autox.event;

/**
 * Dummy automation listener which does not do anything. And is used
 * when no other listener is configured.
 * @author akiran
 */
public class DummyAutomationListener implements IAutomationListener
{
	@Override
	public void testCaseStarted(AutomationEvent event)
	{
	}

	@Override
	public void testCaseCompleted(AutomationEvent event)
	{
	}
}