package com.yukthitech.mail.tracker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
	 * Name extracted from mail id.
	 */
	private String fromName;
	
	/**
	 * Mail id from which mail is received.
	 */
	private String fromMailId;
	
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
	 * Instantiates a new mail message.
	 */
	public ReceivedMailMessage()
	{}

	/**
	 * Instantiates a new received mail message.
	 *
	 * @param fromName the from name
	 * @param fromMailId the from mail id
	 * @param subject the subject
	 */
	public ReceivedMailMessage(String fromName, String fromMailId, String subject)
	{
		this.fromName = fromName;
		this.fromMailId = fromMailId;
		this.subject = subject;
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
	 * Sets the name extracted from mail id.
	 *
	 * @param fromName the new name extracted from mail id
	 */
	public void setFromName(String fromName)
	{
		this.fromName = fromName;
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
	 * Sets the mail id from which mail is received.
	 *
	 * @param fromMailId the new mail id from which mail is received
	 */
	public void setFromMailId(String fromMailId)
	{
		this.fromMailId = fromMailId;
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
	 * Sets the mail message subject.
	 *
	 * @param subject the new mail message subject
	 */
	public void setSubject(String subject)
	{
		this.subject = subject;
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
	 * Sets the attachments received in mail.
	 *
	 * @param attachments the new attachments received in mail
	 */
	public void setAttachments(List<Attachment> attachments)
	{
		this.attachments = attachments;
	}
	
	/**
	 * Adds specified attachment to this mail.
	 * @param attachment Attachment to be added.
	 */
	public void addAttachment(Attachment attachment)
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
	public void setContent(String content)
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
	public void setTextContent(String textContent)
	{
		this.textContent = textContent;
	}
	
	/**
	 * Adds the specified text content to existing content.
	 * @param textContent text content to be added.
	 */
	public void addTextContent(String textContent)
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
}
