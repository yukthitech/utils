package com.yukthitech.autox.ide.xmlfile;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class XmlFile
{
	private Element rootElement;

	/**
	 * Line number till which parsing is done to generate this xml file. If the full content is parsed this
	 * value will be -1.
	 */
	private int parsedTill = -1;
	
	public XmlFile()
	{
	}
	
	public XmlFile(Element rootElement)
	{
		this.rootElement = rootElement;
	}
	
	public Element getElement(String withName, int curLineNo)
	{
		if(!rootElement.hasOffset(curLineNo))
		{
			return null;
		}

		return rootElement.getElement(withName.toLowerCase(), curLineNo);
	}
	
	public static XmlFile parse(String content, int validPosition) throws Exception
	{
		boolean partial = (validPosition > 0);
		
		try
		{
			return XmlFileParser.parse(content);
		}catch(XmlParseException ex)
		{
			if(!partial)
			{
				throw ex;
			}
			
			int posFromEx = ex.getOffset();
			
			if(posFromEx >= validPosition)
			{
				XmlFile xmlFile = ex.getXmlFile();
				xmlFile.parsedTill = validPosition;
				return xmlFile;
			}
			
			throw ex;
		}
	}
	
	public Element getRootElement()
	{
		return rootElement;
	}
	
	public Element getLastElement(int offset)
	{
		return rootElement.getLastElement(offset);
	}
	
	/**
	 * Returns flag indicating if this file object is created by full or partial parsing.
	 * @return
	 */
	public boolean isPartiallyParsed()
	{
		return parsedTill >= 0;
	}
	
	/**
	 * Gets the line number till which parsing is done to generate this xml file. If the full content is parsed this value will be -1.
	 *
	 * @return the line number till which parsing is done to generate this xml file
	 */
	public int getParsedTill()
	{
		return parsedTill;
	}
	
	public String getNamespaceWithPrefix(String prefix)
	{
		return rootElement.getNamespaceWithPrefix(prefix);
	}
	
	public String getPrefixForNamespace(String namespace)
	{
		return rootElement.getPrefixForNamespace(namespace);
	}
	
	@Override
	public String toString()
	{
		return super.toString() + " - " + rootElement.toString();
	}
	
	public static void main(String[] args) throws Exception
	{
		XmlFile.parse(FileUtils.readFileToString(new File("./dml-test-suite.xml")), -1);
	}
}
