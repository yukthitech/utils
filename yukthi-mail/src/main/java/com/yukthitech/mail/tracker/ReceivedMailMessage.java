package com.yukthitech.mail.tracker;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.mail.Message;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Represents Mail message received. 
 * @author akiran
 */
public class ReceivedMailMessage
{
	/**
	 * Represent mail attachment in received mail.
	 * @author akiran
	 */
	public static class Attachment
	{
		/**
		 * Attachment content in temp file.
		 */
		private File file;
		
		/**
		 * Name of the attachment.
		 */
		private String name;
		
		/**
		 * Instantiates a new attachment.
		 */
		public Attachment()
		{}

		/**
		 * Instantiates a new attachment.
		 *
		 * @param file the file
		 * @param name the name
		 */
		public Attachment(File file, String name)
		{
			this.file = file;
			this.name = name;
		}

		/**
		 * Gets the attachment content in temp file.
		 *
		 * @return the attachment content in temp file
		 */
		public File getFile()
		{
			return file;
		}

		/**
		 * Sets the attachment content in temp file.
		 *
		 * @param file the new attachment content in temp file
		 */
		public void setFile(File file)
		{
			this.file = file;
		}

		/**
		 * Gets the name of the attachment.
		 *
		 * @return the name of the attachment
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * Sets the name of the attachment.
		 *
		 * @param name the new name of the attachment
		 */
		public void setName(String name)
		{
			this.name = name;
		}
	}
	
	/**
	 * The temp mail folder.
	 */
	private static File TEMP_MAIL_FOLDER = new File(".mail.temp");
	
	/**
	 * The uq id.
	 */
	private static AtomicInteger UQ_ID = new AtomicInteger(1);
	
	static
	{
		if(!TEMP_MAIL_FOLDER.exists())
		{
			try
			{
				FileUtils.forceMkdir(TEMP_MAIL_FOLDER);
			}catch(Exception  ex)
			{
				throw new InvalidStateException("An error occurred while creating temp mail folder: " + TEMP_MAIL_FOLDER.getPath(), ex);
			}
		}
	}
	
	/**
	 * uid of the mail.
	 */
	private long uid;
	
	/**
	 * Name extracted from mail id.
	 */
	private String fromName;
	
	/**
	 * Mail id from which mail is received.
	 */
	private String fromMailId;
	
	/**
	 * Name extracted from reply to id.
	 */
	private String replyToName;
	
	/**
	 * Reply to mail id.
	 */
	private String replyToMailId;
	
	/**
	 * Mail message subject.
	 */
	private String subject;
	
	/**
	 * Attachments received in mail.
	 */
	private List<Attachment> attachments;
	
	/**
	 * Main content of the mail.
	 */
	private String content;
	
	/**
	 * Context of the mail in textual format.
	 */
	private String textContent;
	
	/**
	 * Document representation of content for easy searching.
	 */
	private Document contentDocument;
	
	/**
	 * The received date.
	 */
	private Date receivedDate;
	
	/**
	 * Flag indicating if this message was read earlier.
	 */
	private boolean readEarlier;
	
	/**
	 * To list;
	 */
	private String toList;
	
	/**
	 * The actual message.
	 */
	private Message actualMessage;
	
	/**
	 * The mail file.
	 */
	private File mailFile;
	
	/**
	 * Instantiates a new received mail message.
	 *
	 * @param uid the uid
	 * @param fromName the from name
	 * @param fromMailId the from mail id
	 * @param subject the subject
	 * @param receivedDate the received date
	 * @param readEarlier the read earlier
	 */
	public ReceivedMailMessage(long uid, 
			String fromName, String fromMailId,
			String replyToName, String replyToMailId,
			String subject, Date receivedDate, boolean readEarlier, String toList, Message actualMessage)
	{
		this.uid = uid;
		this.fromName = fromName;
		this.fromMailId = fromMailId;
		this.replyToName = replyToName;
		this.replyToMailId = replyToMailId;
		this.subject = subject;
		this.receivedDate = receivedDate;
		this.readEarlier = readEarlier;
		this.toList = toList;
		this.actualMessage = actualMessage;
	}
	
	/**
	 * Gets the to list;.
	 *
	 * @return the to list;
	 */
	public String getToList()
	{
		return toList;
	}
	
	/**
	 * Gets the uid of the mail.
	 *
	 * @return the uid of the mail
	 */
	public long getUid()
	{
		return uid;
	}
	
	/**
	 * Sets the name extracted from mail id.
	 *
	 * @param fromName the new name extracted from mail id
	 */
	public void setFromName(String fromName)
	{
		this.fromName = fromName;
	}

	/**
	 * Sets the mail id from which mail is received.
	 *
	 * @param fromMailId the new mail id from which mail is received
	 */
	public void setFromMailId(String fromMailId)
	{
		this.fromMailId = fromMailId;
	}

	/**
	 * Gets the name extracted from mail id.
	 *
	 * @return the name extracted from mail id
	 */
	public String getFromName()
	{
		return fromName;
	}

	/**
	 * Gets the mail id from which mail is received.
	 *
	 * @return the mail id from which mail is received
	 */
	public String getFromMailId()
	{
		return fromMailId;
	}

	/**
	 * Gets the mail message subject.
	 *
	 * @return the mail message subject
	 */
	public String getSubject()
	{
		return subject;
	}

	/**
	 * Gets the attachments received in mail.
	 *
	 * @return the attachments received in mail
	 */
	public List<Attachment> getAttachments()
	{
		return attachments;
	}

	/**
	 * Adds specified attachment to this mail.
	 * @param attachment Attachment to be added.
	 */
	void addAttachment(Attachment attachment)
	{
		if(this.attachments == null)
		{
			this.attachments = new ArrayList<>();
		}
		
		this.attachments.add(attachment);
	}

	/**
	 * Gets the main content of the mail.
	 *
	 * @return the main content of the mail
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * Sets the main content of the mail.
	 *
	 * @param content the new main content of the mail
	 */
	void setContent(String content)
	{
		this.content = content;
		this.contentDocument = null;
	}
	
	/**
	 * Checks if the content is set on this mail.
	 * @return true if content is present.
	 */
	public boolean hasContent()
	{
		return content != null;
	}

	/**
	 * Gets the context of the mail in textual format.
	 *
	 * @return the context of the mail in textual format
	 */
	public String getTextContent()
	{
		return textContent;
	}

	/**
	 * Sets the context of the mail in textual format.
	 *
	 * @param textContent the new context of the mail in textual format
	 */
	void setTextContent(String textContent)
	{
		this.textContent = textContent;
	}
	
	/**
	 * Gets the received date.
	 *
	 * @return the received date
	 */
	public Date getReceivedDate()
	{
		return receivedDate;
	}
	
	/**
	 * Gets the name extracted from reply to id.
	 *
	 * @return the name extracted from reply to id
	 */
	public String getReplyToName()
	{
		return replyToName;
	}

	/**
	 * Gets the reply to mail id.
	 *
	 * @return the reply to mail id
	 */
	public String getReplyToMailId()
	{
		return replyToMailId;
	}

	/**
	 * Checks if is flag indicating if this message was read earlier.
	 *
	 * @return the flag indicating if this message was read earlier
	 */
	public boolean isReadEarlier()
	{
		return readEarlier;
	}

	/**
	 * Adds the specified text content to existing content.
	 * @param textContent text content to be added.
	 */
	void addTextContent(String textContent)
	{
		if(this.textContent != null)
		{
			this.textContent += textContent;
		}
		
		this.textContent = textContent;
	}

	/**
	 * Builds the document from content if it is not present already.
	 */
	private void buildDocument() throws MailProcessingException
	{
		if(this.contentDocument != null)
		{
			return;
		}
		
		if(content == null)
		{
			throw new MailProcessingException("HTML content methods are invoked on non-html based emails");
		}
		
		this.contentDocument = Jsoup.parse(content);
	}
	
	/**
	 * Gets the document representation of content for easy searching.
	 *
	 * @return the document representation of content for easy searching
	 */
	public Document getContentDocument() throws MailProcessingException
	{
		buildDocument();
		return contentDocument;
	}

	/**
	 * Gets the html of the element with specified id.
	 * @param id Id of the element to be queried.
	 * @return Matching element html
	 */
	public String getHtmlById(String id) throws MailProcessingException
	{
		buildDocument();
		
		Element element = contentDocument.getElementById(id);
		
		if(element == null)
		{
			return null;
		}
		
		return element.html();
	}

	/**
	 * Gets the text of the element with specified id.
	 * @param id Id of the element to be queried.
	 * @return Matching element text
	 */
	public String getTextById(String id) throws MailProcessingException
	{
		buildDocument();
		
		Element element = contentDocument.getElementById(id);
		
		if(element == null)
		{
			return null;
		}
		
		return element.html();
	}

	/**
	 * Gets the html of the first element with specified class.
	 * @param cssClass CSS Class of the element to be queried.
	 * @return Matching element html
	 */
	public String getHtmlByClass(String cssClass) throws MailProcessingException
	{
		buildDocument();
		
		Elements elements = contentDocument.getElementsByClass(cssClass);
		
		if(elements == null || elements.size() <= 0)
		{
			return null;
		}
		
		Element element = elements.first();
		return element.html();
	}

	/**
	 * Gets the text of the first element with specified class.
	 * @param cssClass Id of the element to be queried.
	 * @return Matching element text
	 */
	public String getTextByClass(String cssClass) throws MailProcessingException
	{
		buildDocument();
		
		Elements elements = contentDocument.getElementsByClass(cssClass);
		
		if(elements == null || elements.size() <= 0)
		{
			return null;
		}
		
		Element element = elements.first();
		return element.text();
	}

	/**
	 * Gets the html of the first element with specified selector.
	 * @param cssSelector CSS selector of the element to be queried.
	 * @return Matching element html
	 */
	public String getHtmlBySelector(String cssSelector) throws MailProcessingException
	{
		buildDocument();
		
		Elements elements = contentDocument.select(cssSelector);
		
		if(elements == null || elements.size() <= 0)
		{
			return null;
		}
		
		Element element = elements.first();
		return element.html();
	}

	/**
	 * Gets the text of the first element with specified selector.
	 * @param cssSelector CSS selector of the element to be queried.
	 * @return Matching element text
	 */
	public String getTextBySelector(String cssSelector) throws MailProcessingException
	{
		buildDocument();
		
		Elements elements = contentDocument.select(cssSelector);
		
		if(elements == null || elements.size() <= 0)
		{
			return null;
		}
		
		Element element = elements.first();
		return element.text();
	}

	/**
	 * Gets the html of the first element with specified name.
	 * @param name Name of the element to be queried.
	 * @return Matching element html
	 */
	public String getHtmlByName(String name) throws MailProcessingException
	{
		buildDocument();
		
		Elements elements = contentDocument.getElementsByAttributeValue("name", name);
		
		if(elements == null || elements.size() <= 0)
		{
			return null;
		}
		
		Element element = elements.first();
		return element.html();
	}

	/**
	 * Gets the text of the first element with specified name.
	 * @param name Name of the element to be queried.
	 * @return Matching element text
	 */
	public String getTextByName(String name) throws MailProcessingException
	{
		buildDocument();
		
		Elements elements = contentDocument.getElementsByAttributeValue("name", name);
		
		if(elements == null || elements.size() <= 0)
		{
			return null;
		}
		
		Element element = elements.first();
		return element.text();
	}
	
	/**
	 * Gets the eml file.
	 *
	 * @return the eml file
	 */
	public File getEmlFile()
	{
		if(mailFile != null && mailFile.exists())
		{
			return mailFile;
		}

		try
		{
			File file = new File(TEMP_MAIL_FOLDER, "mail-" + UQ_ID.getAndIncrement() + ".eml");
			FileOutputStream fos = new FileOutputStream(file);
			
			actualMessage.writeTo(fos);
			
			fos.flush();
			fos.close();
			
			this.mailFile = file;
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while saving mail file", ex);
		}
		
		return mailFile;
	}
}
