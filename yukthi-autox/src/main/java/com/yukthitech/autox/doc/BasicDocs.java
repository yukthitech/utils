package com.yukthitech.autox.doc;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents basic static docs.
 * @author akiran
 */
public class BasicDocs
{
	/**
	 * Document instance.
	 * @author akiran
	 */
	public static class Document
	{
		/**
		 * Title of the document.
		 */
		private String title;
		
		/**
		 * Resource file which contains the content.
		 */
		private String file;
		
		/**
		 * Actual content.
		 */
		private String content;

		/**
		 * Gets the title of the document.
		 *
		 * @return the title of the document
		 */
		public String getTitle()
		{
			return title;
		}

		/**
		 * Sets the title of the document.
		 *
		 * @param title the new title of the document
		 */
		public void setTitle(String title)
		{
			this.title = title;
		}

		/**
		 * Gets the resource file which contains the content.
		 *
		 * @return the resource file which contains the content
		 */
		public String getFile()
		{
			return file;
		}

		/**
		 * Sets the resource file which contains the content.
		 *
		 * @param file the new resource file which contains the content
		 */
		public void setFile(String file)
		{
			this.file = file;
		}

		/**
		 * Gets the actual content.
		 *
		 * @return the actual content
		 */
		public String getContent()
		{
			return content;
		}

		/**
		 * Sets the actual content.
		 *
		 * @param content the new actual content
		 */
		public void setContent(String content)
		{
			this.content = content;
		}
	}
	
	/**
	 * List of basic docs.
	 */
	private List<Document> documents = new ArrayList<>();

	/**
	 * Gets the list of basic docs.
	 *
	 * @return the list of basic docs
	 */
	public List<Document> getDocuments()
	{
		return documents;
	}

	/**
	 * Sets the list of basic docs.
	 *
	 * @param documents the new list of basic docs
	 */
	public void setDocuments(List<Document> documents)
	{
		this.documents = documents;
	}
	
	/**
	 * Adds the document.
	 *
	 * @param doc the document to add
	 */
	public void addDocument(Document doc)
	{
		this.documents.add(doc);
	}
}
