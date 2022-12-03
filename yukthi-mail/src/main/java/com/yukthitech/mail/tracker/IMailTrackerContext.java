/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.mail.tracker;

import java.util.Set;

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
	public ReceivedMailMessage getOriginalMessage();
	
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
