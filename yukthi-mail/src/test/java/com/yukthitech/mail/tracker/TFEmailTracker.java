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

import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TFEmailTracker
{
	public static class MailProcessor implements IMailProcessor
	{
		@Override
		public boolean process(IMailTrackerContext context, ReceivedMailMessage mailMessage)
		{
			String subject = mailMessage.getSubject();
			System.out.println("Mail - " + subject);
			
			if(subject.contains("kk-read"))
			{
				return true;
			}
			
			if(subject.contains("kk-delete"))
			{
				context.delete();
			}
			else if(subject.contains("kk-move"))
			{
				context.moveToFolder("processed");
			}
			
			return false;
		}
	}
	
	@Test
	public void testReadMails() throws Exception
	{
		ObjectMapper mapper = new ObjectMapper();
		EmailServerSettings settings = mapper.readValue(TFEmailTracker.class.getResourceAsStream("/email-settings.json"), EmailServerSettings.class);
		
		EmailTracker tracker = new EmailTracker(settings, settings, new MailProcessor());
		tracker.startTracking();
		
		tracker.join();
	}
}
