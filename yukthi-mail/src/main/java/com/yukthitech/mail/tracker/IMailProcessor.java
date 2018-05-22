package com.yukthitech.mail.tracker;

import java.util.Date;

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
	
	/**
	 * Informs the client application about the updated last read time. Next mails will
	 * be processed only after this timestamp only.
	 * @param time time to update
	 * @return Date to be used for next read. Generally this should match with input time.
	 */
	public Date setLastReadTime(Date time);
}
