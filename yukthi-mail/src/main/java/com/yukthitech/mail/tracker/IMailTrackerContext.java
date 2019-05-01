package com.yukthitech.mail.tracker;

import java.util.Set;

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
	
	/**
	 * Forwards current mail to specified mail ids.
	 * @param mailIds mail ids to which this message should be forwarded.
	 * @param newContent new content to be added in starting.
	 * @throws MailProcessingException
	 */
	public void forwardTo(Set<String> mailIds, String newContent) throws MailProcessingException;
}
