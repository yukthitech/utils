package com.yukthitech.autox.event;

/**
 * Automation listener to listen for automation events.
 * @author akiran
 */
public interface IAutomationListener
{
	/**
	 * Invoked when test case is started.
	 * @param event Automation event.
	 */
	public void testCaseStarted(AutomationEvent event);
	
	/**
	 * Invoked when test case is completed.
	 * @param event Automation event.
	 */
	public void testCaseCompleted(AutomationEvent event);
}
