package com.yukthitech.mail.tracker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
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
		private boolean moved;
		
		/**
		 * Folder to which this message should be moved.
		 */
		private String folderToMove;
		
		/**
		 * Flag indicating if mail was processed.
		 */
		private boolean processed;

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
		
		private void applyChanges(Folder folder, Map<String, Message> mssgMap)
		{
			if(folderToMove == null && flagsToApply.isEmpty() && flagsToRemove.isEmpty())
			{
				return;
			}
			
			Folder sourceFolder = (Folder) folder;
			Folder destFolder = null;
			Store store = sourceFolder.getStore();
			
			String mssgId = mailMessage.getUid();

			try
			{
				Message message = mssgMap.get(mssgId);
				
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
			moved = true;
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
			moved = true;
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
		readSession = newSession(readSettings, readSettings.getReadProtocol());
		sendSession = newSession(sendSettings, null);
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
						logger.debug("Processing mails completed. Going to sleep for {} millis.", checkTimeGap);
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
	private Session newSession(EmailServerSettings settings, MailReadProtocol protocol)
	{
		Session mailSession = null;
		Properties configProperties = settings.toProperties(protocol);
		
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
	void extractMailContent(ReceivedMailMessage mailMessage, Object content, String contentType) throws MessagingException, IOException
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
	
	private String[] extractNameAndId(String nameMailId)
	{
		String mailId = nameMailId;
		String name = null;

		if(nameMailId.contains("<"))
		{
			name = nameMailId.substring(0, nameMailId.indexOf("<")).trim();
			mailId = nameMailId.substring(nameMailId.indexOf("<") + 1, nameMailId.indexOf(">")).trim();
		}
		else
		{
			name = nameMailId.substring(0, nameMailId.indexOf("@")).trim();
		}

		// remove non alpha characters
		name = name.replaceAll("[^a-zA-Z]", " ").replaceAll("\\s+", " ").trim();
		
		return new String[] {name, mailId};
	}
	
	/**
	 * Convert message.
	 * 
	 * @param mailId Id of the message
	 * @param message the message
	 * @param folder the folder
	 * @param lastReadTimeStr the last read time str
	 * @return the received mail message
	 * @throws Exception the exception
	 */
	private ReceivedMailMessage convertMessage(String mailId, Message message, Folder folder) throws Exception
	{
		Date recvDate = message.getReceivedDate();
		recvDate = (recvDate == null) ? message.getSentDate() : recvDate;
		
		boolean isProcessed = message.isSet(Flag.FLAGGED);
		
		if(isProcessed)
		{
			logger.trace("Ignoring message as it is already processed. Message subject: {}", message.getSubject());
			return null;
		}

		boolean isReadEarlier = message.isSet(Flag.SEEN);

		String subject = message.getSubject();

		String frmNameMail[] = extractNameAndId(message.getFrom()[0].toString());
		String replyToNameMail[] = extractNameAndId(message.getReplyTo()[0].toString());

		String toLst = Arrays.asList(message.getAllRecipients())
				.stream()
				.map(addr -> addr.toString())
				.collect(Collectors.joining(","));

		ReceivedMailMessage mailMessage = new ReceivedMailMessage(mailId, 
				frmNameMail[0], frmNameMail[1],
				replyToNameMail[0], replyToNameMail[1],
				subject, recvDate, isReadEarlier, toLst, this, message);
		//extractMailContent(mailMessage, message.getContent(), message.getContentType());
		
		//Generate eml file of the mail.
		//mailMessage.getEmlFile();
		
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
	private List<MailProcessingContext> readMailsFromFolder(String folderName, Store store, Set<String> processedMailIds) throws MessagingException, IOException
	{
		logger.debug("Reading mails from folder: {}.", folderName);

		Folder mailFolder = store.getFolder(folderName);
		
		mailFolder.open(Folder.READ_WRITE);

		Message newMessages[] = mailFolder.getMessages();

		if(newMessages.length <= 0)
		{
			logger.debug("No new messages are found. Total messages: {}", 
					mailFolder.getMessageCount());
			return null;
		}
		else
		{
			logger.debug("Number of messages read: " + newMessages.length);
		}

		int count = newMessages.length;
		
		List<MailProcessingContext> mssgLst = new LinkedList<>();
		
		Set<String> tmpMailIds = mailProcessor.getProcessedMailIds();
		Set<String> prevProcessedMailIds = (tmpMailIds == null) ? Collections.emptySet() : tmpMailIds;
		
		final Message finalMssgs[] = newMessages;
		
		CountDownLatch latch = new CountDownLatch(count);
		
		for(int i = 0; i < count; i++)
		{
			final int index = i;
			
			scheduledExecutorService.execute(() -> 
			{
				try
				{
					logger.debug("Checking mail: " + index);
					
					final Message message = finalMssgs[index];
					String mailId = mailProcessor.getUniqueMessageId(mailFolder, message);
					
					//if the current mail was already processed earlier
					if(prevProcessedMailIds.contains(mailId))
					{
						synchronized(processedMailIds)
						{
							processedMailIds.add(mailId);
						}

						return;
					}
					
					logger.debug("Processing mail: " + index);
					
					ReceivedMailMessage mailMessage = null;
					
					try
					{
						mailMessage = convertMessage(mailId, message, mailFolder);
					}catch(Exception ex)
					{
						logger.error("An error occurred while reading message with subject: {}", message.getSubject(), ex);
						return;
					}
					
					if(mailMessage == null)
					{
						return;
					}
					
					MailProcessingContext context = processMail(mailMessage);
					
					synchronized(mssgLst)
					{
						mssgLst.add(context);	
					}
					
					if(context.processed)
					{
						synchronized(processedMailIds)
						{
							processedMailIds.add(mailId);
						}
					}
				}catch(Exception ex)
				{
					logger.error("An error occurred while converting mail message", ex);
				} finally
				{
					latch.countDown();
				}
			});
		}
		
		logger.debug("Waiting for messages to be converted..");
		
		try
		{
			latch.await();
		}catch(InterruptedException ex)
		{
			throw new InvalidStateException("An error occcurred during conversion", ex);
		}
		
		mailFolder.close(true);
		return mssgLst;
	}
	
	private MailProcessingContext processMail(ReceivedMailMessage mssg)
	{
		MailProcessingContext mailProcessingContext = new MailProcessingContext(mssg);
		
		try
		{
			// process the mail
			boolean isMailProcessed = false;

			try
			{
				logger.debug("Sending mail for processing mail with subject: {} [Recv Time: {}]", 
						mssg.getSubject(), TIME_FORMAT.format(mssg.getReceivedDate()));
				isMailProcessed = mailProcessor.process(mailProcessingContext, mssg);
				
				mailProcessingContext.processed = isMailProcessed;
			} catch(Exception ex)
			{
				logger.error("An error occurred while processing mail with subject: {}", mssg.getSubject(), ex);
			}
			
			//if mail content is opened
			if(mssg.isContentRead())
			{
				//and if mail is not moved/deleted and was not read earlier
				if(!mailProcessingContext.moved && !mssg.isReadEarlier())
				{
					//mark it as unread again
					mailProcessingContext.flagsToRemove.add(Flags.Flag.SEEN);
				}
			}
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while processing mail: {}", mssg.getSubject(), ex);
		} finally
		{
			if(mssg.isEmlFileCreated())
			{
				mssg.getEmlFile().delete();
			}
		}
		
		return mailProcessingContext;
	}
	
	/**
	 * Process mails.
	 *
	 * @param messages the messages
	 */
	/*
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
	*/
	
	private void startPostProcessing(List<MailProcessingContext> mailContexts, Store store, Set<String> newMailIds) throws MessagingException
	{
		logger.debug("Doing post processing for {} mail contexts", mailContexts.size());
		
		Folder mailFolder = store.getFolder(readSettings.getFolderName());
		mailFolder.open(Folder.READ_WRITE);
		
		Message newMessages[] = mailFolder.getMessages();
		Map<String, Message> mssgMap = Arrays.asList(newMessages)
				.stream()
				.collect(Collectors.toMap(mssg -> mailProcessor.getUniqueMessageId(mailFolder, mssg), mssg -> mssg));
		
		for(MailProcessingContext context : mailContexts)
		{
			context.applyChanges(mailFolder, mssgMap);
		}
		
		mailProcessor.setProcessedMailIds(newMailIds);
		mailFolder.close(true);
	}
	
	void saveEmlContent(Message message, File file)
	{
		Store store = null;
		
		try
		{
			store = readSession.getStore(readSettings.getReadProtocol().getName());
			store.connect(readSettings.getReadHost(), readSettings.getReadPort(), readSettings.getUserName(), readSettings.getPassword());
			
			Folder mailFolder = store.getFolder(readSettings.getFolderName());
			mailFolder.open(Folder.READ_WRITE);
			
			/*
			Message message = null;
			
			if(mailFolder instanceof UIDFolder)
			{
				UIDFolder uidFolder = (UIDFolder) mailFolder;
				Long id = Long.parseLong(uniqueId);
				
				message = uidFolder.getMessageByUID(id);
			}
			else
			{
				Message mssgs[] = mailFolder.getMessages();
				
				for(Message mssg : mssgs)
				{
					if(uniqueId.equals(mailProcessor.getUniqueMessageId(mailFolder, mssg)))
					{
						message = mssg;
						break;
					}
				}
			}
			
			if(message == null)
			{
				throw new InvalidStateException("No mail found with specified id: {}", uniqueId);
			}
			*/
			
			Message emlMssg = new MimeMessage(readSession);
			
			//set from and to addresses
			emlMssg.setFrom(message.getFrom()[0]);
			
			Address address[] = message.getRecipients(RecipientType.TO);
			
			if(address != null && address.length > 0)
			{
				emlMssg.setRecipients(RecipientType.TO, address);
			}
			
			address = message.getRecipients(RecipientType.CC);
			
			if(address != null && address.length > 0)
			{
				emlMssg.setRecipients(RecipientType.CC, address);
			}
			
			emlMssg.setSubject(message.getSubject());
			
			//set the body content
			Multipart multipart = new MimeMultipart();
			
			String contentType = message.getContentType();
			Object content = message.getContent();
			
			if(!contentType.toLowerCase().contains("multipart"))
			{
				BodyPart mailBodyPart = new MimeBodyPart();
				mailBodyPart.setContent(content, contentType);
				multipart.addBodyPart(mailBodyPart);
			}
			else
			{
				Multipart mssgMultipart = (Multipart) content;
				int count = mssgMultipart.getCount();
				BodyPart part = null;

				for(int i = 0; i < count; i++)
				{
					part = mssgMultipart.getBodyPart(i);
					multipart.addBodyPart(part);
				}
			}
			
			emlMssg.setContent(multipart);
			
			//save the eml message
			FileOutputStream fos = new FileOutputStream(file);
			
			emlMssg.writeTo(fos);
			
			fos.flush();
			fos.close();
			
			mailFolder.close(true);
			store.close();
		} catch(Exception e)
		{
			throw new IllegalStateException("Exception occured while reading the mail ", e);
		}
	}

	/**
	 * Reads the mails from the email server specified by settings.
	 */
	public void readMails()
	{
		List<MailProcessingContext> mssgs = null;
		Store store = null;
		Set<String> newMailIds = new HashSet<String>();
		
		try
		{
			logger.debug("Connecting to store using settings: [Protocol: {}, Host: {}, Port: {}, User: {}]", 
					readSettings.getReadProtocol().getName(), readSettings.getReadHost(), readSettings.getReadPort(), readSettings.getUserName());
			store = readSession.getStore(readSettings.getReadProtocol().getName());
			store.connect(readSettings.getReadHost(), readSettings.getReadPort(), readSettings.getUserName(), readSettings.getPassword());

			try
			{
				mssgs = readMailsFromFolder(readSettings.getFolderName(), store, newMailIds);
			} finally
			{
				store.close();
			}
			
			if(CollectionUtils.isEmpty(mssgs))
			{
				logger.debug("No new mails found from last read time..");
				return;
			}
			
			//logger.debug("Reading mails completed. Processing the mails...");
			//List<MailProcessingContext> mailContexts = processMails(mssgs);

			logger.debug("Processing mails completed. Doing post process..");
			store = readSession.getStore(readSettings.getReadProtocol().getName());
			store.connect(readSettings.getReadHost(), readSettings.getReadPort(), readSettings.getUserName(), readSettings.getPassword());
			
			try
			{
				startPostProcessing(mssgs, store, newMailIds);
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
