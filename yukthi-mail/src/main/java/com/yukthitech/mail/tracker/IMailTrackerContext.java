package com.yukthitech.mail.tracker;

import javax.mail.Message;

/**
 * Context object sent during processing mails.
 * @author akiran
 */
public interface IMailTrackerContext
{
	/**
	 * Returns the original mail message received.
	 * @return original mail
	 */
	public Message getOriginalMessage();
	
	/**
	 * Marks the mail for deleting, which would happen at end of processing the mail.
	 */
	public void delete() throws MailProcessingException;
	
	/**
	 * Moves the current mail to specified folder.
	 * @param folder folder to move.
	 */
	public void moveToFolder(String folder) throws MailProcessingException;
}
