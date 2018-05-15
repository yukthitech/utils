package com.yukthitech.mail.tracker;

/**
 * Abstraction of mail processors.
 * @author akiran
 */
public interface IMailProcessor
{
	/**
	 * Called to process mails. 
	 * @param context Context for mail processing.
	 * @param mailMessage Mail to be processed.
	 * @return true if processed
	 */
	public boolean process(IMailTrackerContext context, ReceivedMailMessage mailMessage);
}
