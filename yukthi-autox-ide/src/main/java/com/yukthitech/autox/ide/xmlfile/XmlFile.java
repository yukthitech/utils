package com.yukthitech.autox.ide.xmlfile;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

public class XmlFile
{
	private Element rootElement;

	public XmlFile(Element rootElement)
	{
		this.rootElement = rootElement;
	}
	
	public Element getElement(String withName, int curLineNo)
	{
		if(!rootElement.hasLineNumber(curLineNo))
		{
			return null;
		}

		return rootElement.getElement(withName.toLowerCase(), curLineNo);
	}

	public static XmlFile parse(String content) throws Exception
	{
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new StringReader(content));
		
		Element curElement = null, rootElement = null;
		
		while(xmlStreamReader.hasNext())
		{
			xmlStreamReader.next();
			
			if(xmlStreamReader.getEventType() == XMLEvent.START_ELEMENT)
			{
				Element newElement = new Element(curElement, xmlStreamReader.getNamespaceURI(), xmlStreamReader.getLocalName(), xmlStreamReader.getLocation().getLineNumber());
				
				if(curElement != null)
				{
					curElement.addElement(newElement);
				}
				
				curElement = newElement;
				
				if(rootElement == null)
				{
					rootElement = curElement;
				}
				
				int attrCount = xmlStreamReader.getAttributeCount();
				
				for(int i = 0; i < attrCount; i++)
				{
					curElement.addAttribute(new Attribute(xmlStreamReader.getAttributeNamespace(i), xmlStreamReader.getAttributeLocalName(i), xmlStreamReader.getAttributeValue(i)));
				}
			}
			else if(xmlStreamReader.getEventType() == XMLEvent.END_ELEMENT)
			{
				curElement.setEndLineNo(xmlStreamReader.getLocation().getLineNumber());
				curElement = curElement.getParentElement();
			}
		}
		
		return new XmlFile(rootElement);
	}
	
	public Element getRootElement()
	{
		return rootElement;
	}
	
	@Override
	public String toString()
	{
		return super.toString() + " - " + rootElement.toString();
	}
}
