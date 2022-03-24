package com.yukthitech.autox;

/**
 * Listener to listen to step events which can be added to automation context.
 * @author akiran
 */
public interface IStepListener
{
	public void stepStarted(IStep step);
	
	/**
	 * Expected to be used by steps to inform about intermediate phases with messages.
	 * @param step
	 * @param mssg
	 */
	public void stepPhase(IStep step, String mssg);
	
	public void stepCompleted(IStep step);
	
	public void stepErrored(IStep step, Exception ex);
}
