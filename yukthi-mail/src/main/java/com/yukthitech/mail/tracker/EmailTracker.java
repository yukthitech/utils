package com.yukthitech.mail.tracker;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Tracks the incoming emails and on receival of new mail, registered mail
 * processor will be invoked.
 * 
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
	
	private static final Flags PROCESSED_FLAGS = new Flags(Flags.Flag.FLAGGED);

	/**
	 * Context object to be used while processing mails.
	 * 
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
		 * @param message
		 *            the message
		 * @param sourceFolder
		 *            the source folder
		 */
		public MailProcessingContext(Message message, Folder sourceFolder)
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
				logger.debug("Deleting mail with subject: {}", message.getSubject());
				
				message.setFlag(Flags.Flag.DELETED, true);
				processed = true;
			} catch(Exception ex)
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
				logger.debug("Moving to folder {} the mail with subject: {}", folder, message.getSubject());
				
				destFolder = sourceFolder.getFolder(folder);

				if(destFolder == null || !destFolder.exists())
				{
					throw new MailProcessingException("No folder exists with specified name '{}' under folder: {}", folder, sourceFolder.getName());
				}

				destFolder.open(Folder.READ_WRITE);
			} catch(MessagingException ex)
			{
				throw new MailProcessingException("An erorr occurred while opening specified folder: " + folder, ex);
			}

			try
			{
				sourceFolder.copyMessages(new Message[] { message }, destFolder);
				destFolder.close(false);

				message.setFlag(Flags.Flag.DELETED, true);
				processed = true;
			} catch(MessagingException ex)
			{
				throw new MailProcessingException("An erorr occurred while moving message to folder: " + folder, ex);
			}
		}

		@Override
		public void forwardTo(Set<String> mailIds, String newContent) throws MailProcessingException
		{
			try
			{
				Message fwdMessage = new MimeMessage(session);
				fwdMessage.setSubject("Fwd: " + message.getSubject());
				fwdMessage.setFrom(new InternetAddress(settings.getUserName()));
				
				for(String mailId : mailIds)
				{
					fwdMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(mailId));
				}
				
				String mssgToLst = Arrays.asList(message.getAllRecipients()).stream()
						.map(recep -> recep.toString())
						.collect(Collectors.joining(","));
	
				// Create your new message part
				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setText(String.format(
						"%s\n\n\n-------- Forwarded Message --------:\n"
						+ "<b>Subject:</b> %s\n"
						+ "<b>Date:</b> %s\n"
						+ "<b>From:</b> %s\n"
						+ "<b>To:</b> %s\n\n",
						
						newContent,
						message.getSubject(),
						message.getReceivedDate(),
						message.getFrom()[0].toString(),
						mssgToLst
						));
	
				// Create a multi-part to combine the parts
				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(messageBodyPart);
	
				// Create and fill part for the forwarded content
				messageBodyPart = new MimeBodyPart();
				messageBodyPart.setDataHandler(message.getDataHandler());
	
				// Add part to multi part
				multipart.addBodyPart(messageBodyPart);
	
				// Associate multi-part with message
				fwdMessage.setContent(multipart);
	
				// Send message
				Transport.send(fwdMessage);
			}catch(Exception ex)
			{
				throw new MailProcessingException("An error occurred while forwarding the message", ex);
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
	private Date lastReadTime = null;

	/**
	 * Thread pool to process mails parallel.
	 */
	private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);

	/**
	 * Instantiates a new email tracker.
	 *
	 * @param settings
	 *            the settings
	 * @param processor
	 *            the processor
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
	 * @param checkTimeGap
	 *            the new time gap for checking the mails
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
				logger.debug("Starting reading the mails. Using Timezone: {}", TimeZone.getDefault().getID());

				while(!stopTracking)
				{
					try
					{
						readMails();
					} catch(Exception ex)
					{
						// log the exception
						logger.error("An error occurred while reading mails.", ex);
					}

					try
					{
						logger.trace("Going to sleep for {} millis.", checkTimeGap);
						Thread.sleep(checkTimeGap);
					} catch(Exception ex)
					{
						// ignore
					}
				}

				if(store != null)
				{
					try
					{
						store.close();
					} catch(Exception ex)
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
	 * Informs background thread to stop. Background thread will stop in some
	 * time.
	 */
	public void stopTracking()
	{
		stopTracking = true;
	}

	/**
	 * Returns true if tracking is currently active.
	 * 
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
	 * @param session
	 *            Mail session to be used.
	 * @param store
	 *            Mails store to check.
	 * @param folderName
	 *            Folder name to check.
	 * @param mailProcessor
	 *            Processor for processing mails.
	 * @param currentMailId
	 *            Current mail id whose folder is being read
	 */
	private void readMailsFromFolder(String folderName) throws MessagingException, IOException
	{
		logger.debug("Reading mails from folder: {}. Last read time: {}", folderName, lastReadTime);

		Folder mailFolder = store.getFolder(folderName);
		mailFolder.open(Folder.READ_WRITE);

		Message newMessages[] = null;

		if(lastReadTime != null)
		{
			Date earlyMorning = DateUtils.truncate(lastReadTime, Calendar.DATE);
			earlyMorning = DateUtils.addSeconds(earlyMorning, -1);

			SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GT, earlyMorning);

			newMessages = mailFolder.search(newerThan);
		}
		else
		{
			newMessages = mailFolder.getMessages();
		}

		if(newMessages.length <= 0)
		{
			logger.debug("No new messages are found from last read [{}]. Total messages: {}", lastReadTime, mailFolder.getMessageCount());
			return;
		}

		int count = newMessages.length;
		final AtomicInteger processedCount = new AtomicInteger(0);

		for(int i = 0; i < count; i++)
		{
			final Message message = newMessages[i];
			
			scheduledExecutorService.execute(new Runnable()
			{
				public void run()
				{
					try
					{
						if(lastReadTime != null && lastReadTime.after(message.getReceivedDate()))
						{
							return;
						}
						
						boolean isProcessed = message.isSet(Flag.FLAGGED);
						
						if(isProcessed)
						{
							logger.trace("Ignoring message as it is already processed. Message subject: {}", message.getSubject());
							return;
						}

						boolean isReadEarlier = message.isSet(Flag.SEEN);

						String subject = message.getSubject();

						String nameMailId = message.getFrom()[0].toString();
						String frmMailId = nameMailId;
						String fromName = null;

						if(nameMailId.contains("<"))
						{
							fromName = nameMailId.substring(0, nameMailId.indexOf("<")).trim();
							frmMailId = nameMailId.substring(nameMailId.indexOf("<") + 1, nameMailId.indexOf(">")).trim();
						}
						else
						{
							fromName = nameMailId.substring(0, nameMailId.indexOf("@")).trim();

							// remove non alpha characters
							fromName = fromName.replaceAll("[^a-zA-Z]", " ").replaceAll("\\s+", " ").trim();
						}

						ReceivedMailMessage mailMessage = new ReceivedMailMessage(fromName, frmMailId, subject);
						extractMailContent(mailMessage, message.getContent(), message.getContentType());

						// process the mail
						MailProcessingContext mailProcessingContext = new MailProcessingContext(message, mailFolder);

						boolean isMailProcessed = false;

						try
						{
							isMailProcessed = mailProcessor.process(mailProcessingContext, mailMessage);
						} catch(Exception ex)
						{
							logger.error("An error occurred while processing mail with subject: {}", mailMessage.getSubject(), ex);
						}
						
						if(!isMailProcessed)
						{
							if(!mailProcessingContext.processed && !isReadEarlier)
							{
								message.setFlags(READ_FLAGS, false);
							}
						}
						else
						{
							//mark message as processed
							message.setFlags(PROCESSED_FLAGS, true);
						}
					} catch(Exception ex)
					{
						throw new InvalidStateException("An error occurred while processing mail: {}", message, ex);
					} finally
					{
						processedCount.incrementAndGet();
					}
				}
			});
		}

		// wait for all messages are processed
		while(true)
		{
			int pcount = processedCount.get();

			if(pcount % 10 == 0 || pcount < 10)
			{
				logger.debug("Processed {} messages out of {}", pcount, count);
			}

			if(pcount >= count)
			{
				logger.debug("Completed processing {} messages out of {}", pcount, count);
				break;
			}

			try
			{
				Thread.sleep(5 * 1000);
			} catch(Exception ex)
			{
			}
		}

		Date now = new Date();
		lastReadTime = mailProcessor.setLastReadTime(now);

		if(lastReadTime == null)
		{
			lastReadTime = now;
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
