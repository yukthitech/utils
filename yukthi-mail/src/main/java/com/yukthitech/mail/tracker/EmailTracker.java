package com.yukthitech.mail.tracker;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Tracks the incoming emails and on receival of new mail, registered mail processor will be invoked.
 * @author akiran
 */
public class EmailTracker
{
	private static Logger logger = LogManager.getLogger(EmailTracker.class);
	
	/**
	 * One minute in millis.
	 */
	private static final long ONE_MIN_IN_MILLIS = 60 * 1000;
	
	private static final Flags READ_FLAGS = new Flags(Flags.Flag.SEEN);
	
	/**
	 * Context object to be used while processing mails.
	 * @author akiran
	 */
	private class MailProcessingContext implements IMailTrackerContext
	{
		/**
		 * Message being processed.
		 */
		private Message message;
		
		/**
		 * Fodler in which the current message is found.
		 */
		private Folder sourceFolder;
		
		/**
		 * True if mail is processed internally.
		 */
		private boolean processed;
		
		/**
		 * Instantiates a new mail processing context.
		 *
		 * @param message the message
		 * @param sourceFolder the source folder
		 */
		public MailProcessingContext(Message  message, Folder sourceFolder)
		{
			this.message = message;
			this.sourceFolder = sourceFolder;
		}
		
		@Override
		public Message getOriginalMessage()
		{
			return message;
		}

		@Override
		public void delete() throws MailProcessingException
		{
			try
			{
				message.setFlag(Flags.Flag.DELETED, true);
				processed = true;
			}catch(Exception ex)
			{
				logger.error("An error occurred while deleting the message", ex);
				throw new MailProcessingException("An error occurred while deleting the message", ex);
			}
		}

		@Override
		public void moveToFolder(String folder) throws MailProcessingException
		{
			Folder destFolder = null;
			
			try
			{
				destFolder = sourceFolder.getFolder(folder);
				
				if(destFolder == null || !destFolder.exists())
				{
					throw new MailProcessingException("No folder exists with specified name '{}' under folder: {}", folder, sourceFolder.getName());
				}
				
				destFolder.open(Folder.READ_WRITE);
			}catch(MessagingException ex)
			{
				throw new MailProcessingException("An erorr occurred while opening specified folder: " + folder, ex);
			}
			
			try
			{
				sourceFolder.copyMessages(new Message[]{message}, destFolder);
				destFolder.close(false);
				
				message.setFlag(Flags.Flag.DELETED, true);
				processed = true;
			}catch(MessagingException ex)
			{
				throw new MailProcessingException("An erorr occurred while moving message to folder: " + folder, ex);
			}
		}
	}
	
	/**
	 * Settings to be used for reading mails.
	 */
	private EmailServerSettings settings;
	
	/**
	 * Processor to process the mail.
	 */
	private IMailProcessor mailProcessor;
	
	/**
	 * Current mail session.
	 */
	private Session session;
	
	/**
	 * mail store object.
	 */
	private Store store;
	
	/**
	 * Time gap for checking the mails.
	 */
	private long checkTimeGap = ONE_MIN_IN_MILLIS;
	
	/**
	 * Flag to indicate when tracking should be stopped.
	 */
	private boolean stopTracking = false;
	
	/**
	 * Thread tracking the mails.
	 */
	private Thread trackerThread = null;
	
	/**
	 * Time when last read was done.
	 */
	private Date lastReadTime = new Date();
	
	/**
	 * Instantiates a new email tracker.
	 *
	 * @param settings the settings
	 * @param processor the processor
	 */
	public EmailTracker(EmailServerSettings settings, IMailProcessor processor)
	{
		this.settings = settings;
		this.mailProcessor = processor;
		
		session = newSession();
	}
	
	/**
	 * Sets the time gap for checking the mails.
	 *
	 * @param checkTimeGap the new time gap for checking the mails
	 */
	public void setCheckTimeGap(long checkTimeGap)
	{
		this.checkTimeGap = checkTimeGap;
	}
	
	public void setLastReadTime(Date lastReadTime)
	{
		if(lastReadTime.after(new Date()))
		{
			throw new IllegalArgumentException("Future date is specified for last read time. Specified time: " + lastReadTime);
		}
		
		this.lastReadTime = lastReadTime;
	}
	
	/**
	 * Starts a background thread which would start tracking the mails.
	 */
	public void startTracking()
	{
		if(trackerThread != null)
		{
			throw new InvalidStateException("Tracking is already in progress.");
		}
		
		trackerThread = new Thread(settings.getUserName() + "-Tracker")
		{
			public void run()
			{
				logger.debug("Starting reading the mails..");
				
				while(!stopTracking)
				{
					try
					{
						readMails();
					}catch(Exception ex)
					{
						//log the exception
						logger.error("An error occurred while reading mails.", ex);
					}
					
					try
					{
						logger.trace("Going to sleep for {} millis.", checkTimeGap);
						Thread.sleep(checkTimeGap);
					}catch(Exception ex)
					{
						//ignore
					}
				}
				
				if(store != null)
				{
					try
					{
						store.close();
					}catch(Exception ex)
					{
						logger.error("An error occurred while closing the store.", ex);
					}
				}
				
				trackerThread = null;
			}
		};
		
		trackerThread.start();
	}
	
	/**
	 * Informs background thread to stop. Background thread will stop in some time.
	 */
	public void stopTracking()
	{
		stopTracking = true;
	}
	
	/**
	 * Returns true if tracking is currently active.
	 * @return true if tracking is active
	 */
	public boolean isTracking()
	{
		return (trackerThread != null);
	}
	
	public void join() throws InterruptedException
	{
		trackerThread.join();
	}
	
	/**
	 * Create new java mail session with the configuration provided to the
	 * service.
	 *
	 * @param settings
	 *            Settings to create session.
	 * @return newly created session.
	 */
	private Session newSession()
	{
		Session mailSession = null;
		Properties configProperties = settings.toProperties();

		// if authentication needs to be done provide user name and password
		if(settings.isUseAuthentication())
		{
			mailSession = Session.getInstance(configProperties, new Authenticator()
			{
				protected PasswordAuthentication getPasswordAuthentication()
				{
					return new PasswordAuthentication(settings.getUserName(), settings.getPassword());
				}
			});
		}
		else
		{
			mailSession = Session.getInstance(configProperties);
		}

		return mailSession;
	}


	/**
	 * Extracts mail content into specified mail message.
	 * 
	 * @param mailMessage
	 *            Mail message to which content needs to be fetched.
	 * @param content
	 *            Content to be parsed into mail message.
	 * @param contentType
	 *            Content type.
	 */
	private void extractMailContent(ReceivedMailMessage mailMessage, Object content, String contentType) throws MessagingException, IOException
	{
		if(!contentType.toLowerCase().contains("multipart"))
		{
			mailMessage.addTextContent(content.toString());
			return;
		}

		Multipart multipart = (Multipart) content;
		int count = multipart.getCount();
		BodyPart part = null;
		File attachmentFile = null;

		for(int i = 0; i < count; i++)
		{
			part = multipart.getBodyPart(i);

			if(Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()))
			{
				attachmentFile = File.createTempFile(part.getFileName(), ".attachment");
				((MimeBodyPart) part).saveFile(attachmentFile);

				mailMessage.addAttachment(new ReceivedMailMessage.Attachment(attachmentFile, part.getFileName()));
			}
			else if(part.getContentType().toLowerCase().contains("text/html") && !mailMessage.hasContent())
			{
				String contentStr = IOUtils.toString(part.getInputStream());
				mailMessage.setContent(contentStr);
			}
			else if(part.getContentType().toLowerCase().contains("text"))
			{
				String contentStr = IOUtils.toString(part.getInputStream());
				mailMessage.addTextContent(contentStr);
			}
		}
	}

	/**
	 * Reads mails from specified folder and for each mail, mail-processor will
	 * be invoked.
	 * 
	 * @param session Mail session to be used.
	 * @param store
	 *            Mails store to check.
	 * @param folderName
	 *            Folder name to check.
	 * @param mailProcessor
	 *            Processor for processing mails.
	 * @param currentMailId Current mail id whose folder is being read
	 */
	private void readMailsFromFolder(String folderName) throws MessagingException, IOException
	{
		logger.debug("Reading mails from folder: {}", folderName);
		
		Folder mailFolder = store.getFolder(folderName);
		
		mailFolder.open(Folder.READ_WRITE);
		
		Date earlyMorning = DateUtils.truncate(new Date(), Calendar.DATE);
		earlyMorning = DateUtils.addSeconds(earlyMorning, -1);
		
		SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GT, earlyMorning);
		
		Message newMessages[] = mailFolder.search(newerThan);
		
		if(newMessages.length <= 0)
		{
			logger.debug("No new messages are found from last read [{}]. Total messages: {}", lastReadTime, mailFolder.getMessageCount());
			return;
		}
		
		for(int i = 0; i < newMessages.length; i++)
		{
			Message message = newMessages[i];
			
			if(lastReadTime.after(message.getReceivedDate()))
			{
				continue;
			}
			
			String subject = message.getSubject();

			String nameMailId = message.getFrom()[0].toString();
			String frmMailId = nameMailId;
			
			if(nameMailId.contains("<"))
			{
				frmMailId = nameMailId.substring(nameMailId.indexOf("<") + 1, nameMailId.indexOf(">")).trim();
			}

			ReceivedMailMessage mailMessage = new ReceivedMailMessage(frmMailId, subject);
			extractMailContent(mailMessage, message.getContent(), message.getContentType());

			// process the mail
			MailProcessingContext mailProcessingContext = new MailProcessingContext(message, mailFolder);
			
			boolean isMailProcessed = false;
			
			try
			{
				isMailProcessed = mailProcessor.process(mailProcessingContext, mailMessage);
			}catch(Exception ex)
			{
				logger.error("An error occurred while processing mail with subject: {}", mailMessage.getSubject(), ex);
			}
			
			if(!isMailProcessed)
			{
				if(!mailProcessingContext.processed)
				{
					message.setFlags(READ_FLAGS, false);
				}
			}
		}

		lastReadTime = mailProcessor.setLastReadTime( new Date() );
		
		if(lastReadTime == null)
		{
			lastReadTime = new Date();
		}
		
		mailFolder.close(false);
	}

	/**
	 * Reads the mails from the email server specified by settings.
	 */
	public void readMails()
	{
		try
		{
			if(store == null || !store.isConnected())
			{
				store = session.getStore(settings.getReadProtocol().getName());
				store.connect(settings.getReadHost(), settings.getUserName(), settings.getPassword());
			}
			
			for(String folderName : settings.getFolderNames())
			{
				readMailsFromFolder(folderName);
			}

		} catch(Exception e)
		{
			throw new IllegalStateException("Exception occured while reading the mail ", e);
		}
	}
}
