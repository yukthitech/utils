package com.yukthitech.ccg.xml.writer;

import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Contextual information for generating xml content.
 * @author akranthikiran
 */
public class XmlWriterContext
{
	/**
	 * Current element.
	 */
	private Stack<Element> elementStack = new Stack<Element>();
	
	/**
	 * Parent document.
	 */
	private Document document;
	
	/**
	 * Configuration in use.
	 */
	private XmlWriterConfig writerConfig;

	/**
	 * Instantiates a new xml writer context.
	 *
	 * @param element the element
	 * @param document the document
	 * @param writerConfig the writer config
	 */
	public XmlWriterContext(Element element, Document document, XmlWriterConfig writerConfig)
	{
		this.elementStack.push(element);
		this.document = document;
		this.writerConfig = writerConfig;
	}

	/**
	 * Gets the current element.
	 *
	 * @return the current element
	 */
	public Element getElement()
	{
		return elementStack.peek();
	}
	
	public void startChildElement(String name)
	{
		Element child = document.createElement(name);
		getElement().appendChild(child);
		
		elementStack.push(child);
	}
	
	public void endChildElement()
	{
		if(elementStack.size() <= 1)
		{
			throw new InvalidStateException("No chile element found to close");
		}
		
		elementStack.pop();
	}
	
	public void setAttribute(String name, String value)
	{
		getElement().setAttribute(name, value);
	}
	
	public void addTextElement(String name, String value)
	{
		Element propElem = document.createElement(name);
		getElement().appendChild(propElem);
		
		propElem.appendChild( document.createTextNode(value) );
	}
	
	public void addCdataElement(String name, String value)
	{
		Element propElem = document.createElement(name);
		getElement().appendChild(propElem);
		
		propElem.appendChild( document.createCDATASection(value) );
	}

	/**
	 * Gets the parent document.
	 *
	 * @return the parent document
	 */
	public Document getDocument()
	{
		return document;
	}

	/**
	 * Gets the configuration in use.
	 *
	 * @return the configuration in use
	 */
	public XmlWriterConfig getWriterConfig()
	{
		return writerConfig;
	}
	
	/**
	 * Checks whether specified property name can be added as attribute or not.
	 * @param name
	 * @return
	 */
	public boolean isAttributable(String name)
	{
		return writerConfig.isAttributable(getElement().getTagName(), name);
	}
}
