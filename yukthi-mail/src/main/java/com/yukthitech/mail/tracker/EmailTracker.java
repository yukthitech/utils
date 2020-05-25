package com.yukthitech.mail.tracker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
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
import javax.mail.UIDFolder;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
	
	/**
	 * The logger.
	 */
	private static Logger logger = LogManager.getLogger(EmailTracker.class);

	/**
	 * One minute in millis.
	 */
	private static final long ONE_MIN_IN_MILLIS = 60 * 1000;
	
	/**
	 * The Constant TIME_FORMAT.
	 */
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");

	/**
	 * Context object to be used while processing mails.
	 * 
	 * @author akiran
	 */
	private class MailProcessingContext implements IMailTrackerContext
	{
		/**
		 * The mail message.
		 */
		private ReceivedMailMessage mailMessage;
		
		/**
		 * Flags to apply.
		 */
		private List<Flag> flagsToApply = new ArrayList<>();

		/**
		 * Flags to remove.
		 */
		private List<Flag> flagsToRemove = new ArrayList<>();

		/**
		 * True if mail is processed internally.
		 */
		private boolean processed;
		
		/**
		 * Folder to which this message should be moved.
		 */
		private String folderToMove;

		/**
		 * Instantiates a new mail processing context.
		 *
		 * @param mailMessage the mail message
		 */
		public MailProcessingContext(ReceivedMailMessage mailMessage)
		{
			this.mailMessage = mailMessage;
		}

		/**
		 * Gets the original message.
		 *
		 * @return the original message
		 */
		@Override
		public ReceivedMailMessage getOriginalMessage()
		{
			return mailMessage;
		}
		
		private void applyChanges(UIDFolder folder)
		{
			if(folderToMove == null && flagsToApply.isEmpty() && flagsToRemove.isEmpty())
			{
				return;
			}
			
			Folder sourceFolder = (Folder) folder;
			Folder destFolder = null;
			Store store = sourceFolder.getStore();

			try
			{
				Message message = folder.getMessageByUID(mailMessage.getUid());
				
				logger.debug("Moving to folder {} the mail with subject: {}", folderToMove, mailMessage.getSubject());
				
				destFolder = folderToMove != null ? store.getFolder(folderToMove) : null;

				if(destFolder == null || !destFolder.exists())
				{
					logger.warn("Ignoring move request as requested folder '{}' does not exist under folder: {}", folderToMove, sourceFolder.getName());
				}
				else
				{
					destFolder.open(Folder.READ_WRITE);
					sourceFolder.copyMessages(new Message[] { message }, destFolder);
					destFolder.close(false);
				}

				for(Flag flag : flagsToApply)
				{
					message.setFlag(flag, true);
				}

				for(Flag flag : flagsToRemove)
				{
					message.setFlag(flag, false);
				}
			} catch(MessagingException ex)
			{
				logger.error("An error occurred while post processing mail message '{}'", mailMessage.getSubject(), ex);
			}
		}

		/**
		 * Delete.
		 *
		 * @throws MailProcessingException the mail processing exception
		 */
		@Override
		public void delete() throws MailProcessingException
		{
			flagsToApply.add(Flags.Flag.DELETED);
		}

		/**
		 * Move to folder.
		 *
		 * @param folder the folder
		 * @throws MailProcessingException the mail processing exception
		 */
		@Override
		public void moveToFolder(String folder) throws MailProcessingException
		{
			folderToMove = folder;
			flagsToApply.add(Flag.DELETED);
			processed = true;
		}

		/**
		 * Forward to.
		 *
		 * @param mailIds the mail ids
		 * @param newContent the new content
		 * @throws MailProcessingException the mail processing exception
		 */
		@Override
		public void forwardTo(Set<String> mailIds, String newContent) throws MailProcessingException
		{
			synchronized(sendSettings)
			{
				try
				{
					Message fwdMessage = new MimeMessage(sendSession);
					fwdMessage.setSubject("Fwd: " + mailMessage.getSubject());
					
					if(StringUtils.isNotBlank(sendSettings.getFromId()))
					{
						fwdMessage.setFrom(new InternetAddress(sendSettings.getFromId()));
					}
					else
					{
						fwdMessage.setFrom(new InternetAddress(sendSettings.getUserName()));
					}
					
					for(String mailId : mailIds)
					{
						fwdMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(mailId));
					}
					
					// Create your new message part
					BodyPart messageBodyPart = new MimeBodyPart();
					messageBodyPart.setContent(String.format(
							"%s<br><br>-------- Forwarded Message --------<br>"
							+ "<b>Subject:</b> %s<br>"
							+ "<b>Date:</b> %s<br>"
							+ "<b>From:</b> %s<br>"
							+ "<b>To:</b> %s<br><br>",
							
							newContent,
							mailMessage.getSubject(),
							mailMessage.getReceivedDate(),
							mailMessage.getFromMailId(),
							mailMessage.getToList()
							), "text/html");
					
					//mail message body part
					BodyPart mailBodyPart = new MimeBodyPart();
					mailBodyPart.setContent(mailMessage.getContent(), "text/html");
		
					// Create a multi-part to combine the parts
					Multipart multipart = new MimeMultipart();
					multipart.addBodyPart(messageBodyPart);
					multipart.addBodyPart(mailBodyPart);
					
					if(mailMessage.getAttachments() != null)
					{
						for(ReceivedMailMessage.Attachment attachment : mailMessage.getAttachments())
						{
							MimeBodyPart fileBodyPart = new MimeBodyPart();
							FileDataSource fileSource = new FileDataSource(attachment.getFile());

							fileBodyPart.setDataHandler(new DataHandler(fileSource));
							fileBodyPart.setFileName(attachment.getName());
							
							multipart.addBodyPart(fileBodyPart);
						}
					}
					
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
	}

	/**
	 * Settings to be used for reading mails.
	 */
	private EmailServerSettings readSettings;

	/**
	 * Settings to be used for reading mails.
	 */
	private EmailServerSettings sendSettings;

	/**
	 * Processor to process the mail.
	 */
	private IMailProcessor mailProcessor;

	/**
	 * Read mail session.
	 */
	private Session readSession;
	
	/**
	 * Send mail session.
	 */
	private Session sendSession;
	
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
	 * @param readSettings the read settings
	 * @param sendSettings the send settings
	 * @param processor the processor
	 */
	public EmailTracker(EmailServerSettings readSettings, EmailServerSettings sendSettings, IMailProcessor processor)
	{
		this.readSettings = readSettings;
		this.sendSettings = sendSettings;
		
		this.mailProcessor = processor;
		readSession = newSession(readSettings);
		sendSession = newSession(sendSettings);
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

	/**
	 * Sets the time when last read was done.
	 *
	 * @param lastReadTime the new time when last read was done
	 */
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

		trackerThread = new Thread(readSettings.getUserName() + "-Tracker")
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

	/**
	 * Join.
	 *
	 * @throws InterruptedException the interrupted exception
	 */
	public void join() throws InterruptedException
	{
		trackerThread.join();
	}

	/**
	 * Create new java mail session with the configuration provided to the
	 * service.
	 *
	 * @param readSettings
	 *            Settings to create session.
	 * @return newly created session.
	 */
	private Session newSession(EmailServerSettings settings)
	{
		Session mailSession = null;
		Properties configProperties = settings.toProperties();
		
		logger.debug("Connecting to server using properties: {}", configProperties);

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
	 * Convert message.
	 *
	 * @param message the message
	 * @param folder the folder
	 * @param lastReadTimeStr the last read time str
	 * @return the received mail message
	 * @throws Exception the exception
	 */
	private ReceivedMailMessage convertMessage(Message message, UIDFolder folder, String lastReadTimeStr) throws Exception
	{
		Date recvDate = message.getReceivedDate();
		recvDate = (recvDate == null) ? message.getSentDate() : recvDate;
		
		if(lastReadTime != null && lastReadTime.after(recvDate))
		{
			logger.trace("Ignoring message as it's receive time [{}] older than last read time: {}. Message subject: {}", 
					TIME_FORMAT.format(recvDate), lastReadTimeStr, message.getSubject());
			return null;
		}
		
		boolean isProcessed = message.isSet(Flag.FLAGGED);
		
		if(isProcessed)
		{
			logger.trace("Ignoring message as it is already processed. Message subject: {}", message.getSubject());
			return null;
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
		}

		// remove non alpha characters
		fromName = fromName.replaceAll("[^a-zA-Z]", " ").replaceAll("\\s+", " ").trim();
		
		String toLst = Arrays.asList(message.getAllRecipients())
				.stream()
				.map(addr -> addr.toString())
				.collect(Collectors.joining(","));

		ReceivedMailMessage mailMessage = new ReceivedMailMessage(folder.getUID(message), fromName, 
				frmMailId, subject, recvDate, isReadEarlier, toLst, message);
		extractMailContent(mailMessage, message.getContent(), message.getContentType());
		
		//Generate eml file of the mail.
		mailMessage.getEmlFile();
		
		return mailMessage;
	}

	/**
	 * Reads mails from specified folder and for each mail, mail-processor will
	 * be invoked.
	 * 
	 * @param readSession
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
	private List<ReceivedMailMessage> readMailsFromFolder(String folderName, Store store) throws MessagingException, IOException
	{
		logger.debug("Reading mails from folder: {}. Last read time: {}", folderName, TIME_FORMAT.format(lastReadTime));

		Folder mailFolder = store.getFolder(folderName);
		UIDFolder uidFolder = (UIDFolder) mailFolder;
		
		mailFolder.open(Folder.READ_WRITE);

		Message newMessages[] = mailFolder.getMessages();

		if(readSettings.getReadProtocol() == MailReadProtocol.IMAPS)
		{
			if(lastReadTime != null)
			{
				SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GT, lastReadTime);
				logger.debug("Reading mails from time: {}", TIME_FORMAT.format(lastReadTime));
	
				newMessages = mailFolder.search(newerThan);
			}
			else
			{
				newMessages = mailFolder.getMessages();
			}
		}
		else
		{
			newMessages = mailFolder.getMessages();
		}

		if(newMessages.length <= 0)
		{
			logger.debug("No new messages are found from last read [{}]. Total messages: {}", 
					TIME_FORMAT.format(lastReadTime), mailFolder.getMessageCount());
			return null;
		}

		int count = newMessages.length;
		String lastReadTimeStr = TIME_FORMAT.format(lastReadTime);
		
		List<ReceivedMailMessage> mssgLst = new LinkedList<>();

		for(int i = 0; i < count; i++)
		{
			final Message message = newMessages[i];
			ReceivedMailMessage mailMessage = null;
			
			try
			{
				mailMessage = convertMessage(message, uidFolder, lastReadTimeStr);
			}catch(Exception ex)
			{
				logger.error("An error occurred while reading message with subject: {}", message.getSubject(), ex);
				continue;
			}
			
			if(mailMessage == null)
			{
				continue;
			}
			
			mssgLst.add(mailMessage);
		}
		
		mailFolder.close(true);
		return mssgLst;
	}
	
	/**
	 * Process mails.
	 *
	 * @param messages the messages
	 */
	private List<MailProcessingContext> processMails(List<ReceivedMailMessage> messages)
	{
		final AtomicInteger processedCount = new AtomicInteger(0);
		List<MailProcessingContext> mailContexts = new LinkedList<>();
		
		for(ReceivedMailMessage mssg : messages)
		{
			final MailProcessingContext mailProcessingContext = new MailProcessingContext(mssg);
			mailContexts.add(mailProcessingContext);
			
			scheduledExecutorService.execute(new Runnable()
			{
				public void run()
				{
					try
					{
						// process the mail
						boolean isMailProcessed = false;

						try
						{
							logger.debug("Sending mail for processing mail with subject: {} [Recv Time: {}]", 
									mssg.getSubject(), TIME_FORMAT.format(mssg.getReceivedDate()));
							isMailProcessed = mailProcessor.process(mailProcessingContext, mssg);
						} catch(Exception ex)
						{
							logger.error("An error occurred while processing mail with subject: {}", mssg.getSubject(), ex);
						}
						
						if(!isMailProcessed)
						{
							if(!mailProcessingContext.processed && !mssg.isReadEarlier())
							{
								mailProcessingContext.flagsToRemove.add(Flags.Flag.SEEN);
							}
						}
						else
						{
							//mark message as processed
							mailProcessingContext.flagsToApply.add(Flags.Flag.FLAGGED);
						}
					} catch(Exception ex)
					{
						throw new InvalidStateException("An error occurred while processing mail: {}", mssg.getSubject(), ex);
					} finally
					{
						processedCount.incrementAndGet();
						
						if(mssg.getEmlFile() != null)
						{
							mssg.getEmlFile().delete();
						}
					}
				}
			});
		}

		// wait for all messages are processed
		while(true)
		{
			int pcount = processedCount.get();
			int count = messages.size();
			
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
		
		return mailContexts;
	}
	
	private void startPostProcessing(List<MailProcessingContext> mailContexts, Store store) throws MessagingException
	{
		logger.debug("Doing post processing for {} mail contexts", mailContexts.size());
		
		Folder mailFolder = store.getFolder(readSettings.getFolderName());
		mailFolder.open(Folder.READ_WRITE);
		
		UIDFolder uidFolder = (UIDFolder) mailFolder;
		
		for(MailProcessingContext context : mailContexts)
		{
			context.applyChanges(uidFolder);
		}
		
		mailFolder.close(true);

		Date now = new Date();
		lastReadTime = mailProcessor.setLastReadTime(now);

		if(lastReadTime == null)
		{
			lastReadTime = now;
		}
	}

	/**
	 * Reads the mails from the email server specified by settings.
	 */
	public void readMails()
	{
		List<ReceivedMailMessage> mssgs = null;
		Store store = null;
		
		try
		{
			logger.debug("Connecting to store using settings: [Protocol: {}, Host: {}, Port: {}, User: {}]", 
					readSettings.getReadProtocol().getName(), readSettings.getReadHost(), readSettings.getReadPort(), readSettings.getUserName());
			store = readSession.getStore(readSettings.getReadProtocol().getName());
			store.connect(readSettings.getReadHost(), readSettings.getReadPort(), readSettings.getUserName(), readSettings.getPassword());

			try
			{
				mssgs = readMailsFromFolder(readSettings.getFolderName(), store);
			} finally
			{
				store.close();
			}
			
			if(CollectionUtils.isEmpty(mssgs))
			{
				logger.debug("No new mails found from last read time..");
				return;
			}
			
			logger.debug("Reading mails completed. Processing the mails...");
			List<MailProcessingContext> mailContexts = processMails(mssgs);

			logger.debug("Processing mails completed. Doing post process..");
			store = readSession.getStore(readSettings.getReadProtocol().getName());
			store.connect(readSettings.getReadHost(), readSettings.getReadPort(), readSettings.getUserName(), readSettings.getPassword());
			
			try
			{
				startPostProcessing(mailContexts, store);
			} finally
			{
				store.close();
			}
		} catch(Exception e)
		{
			throw new IllegalStateException("Exception occured while reading the mail ", e);
		}
	}
}
