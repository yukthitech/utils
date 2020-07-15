package com.yukthitech.autox;

import com.yukthitech.autox.test.TestCaseData;

/**
 * Listener to listen to step events which can be added to automation context.
 * @author akiran
 */
public interface IStepListener
{
	public void stepStarted(IStep step, TestCaseData data);
	
	/**
	 * Expected to be used by steps to inform about intermediate phases with messages.
	 * @param step
	 * @param mssg
	 */
	public void stepPhase(IStep step, String mssg);
	
	public void stepCompleted(IStep step, TestCaseData data);
	
	public void stepErrored(IStep step, TestCaseData data, Exception ex);
}
