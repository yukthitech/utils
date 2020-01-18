package com.yukthitech.mail.tracker;

import java.util.Date;

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

		@Override
		public Date setLastReadTime(Date time)
		{
			return time;
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
