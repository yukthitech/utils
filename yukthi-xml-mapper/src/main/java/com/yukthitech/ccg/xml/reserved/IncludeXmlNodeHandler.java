package com.yukthitech.ccg.xml.reserved;

import java.io.FileInputStream;
import java.io.InputStream;

import com.yukthitech.ccg.xml.BeanNode;
import com.yukthitech.ccg.xml.IParserHandler;
import com.yukthitech.ccg.xml.XMLAttributeMap;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.ccg.xml.XMLLoadException;

/**
 * Reserve node handler to load specified resource or xml file.
 * 
 * To specify resource use attribute "resource". To specify file use attribute "file".
 * 
 * @author akiran
 */
@NodeName(namePattern = "includeXml")
public class IncludeXmlNodeHandler implements IReserveNodeHandler
{
	@Override
	public Object createCustomNodeBean(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att)
	{
		String resourcePath = att.get("resource", null);
		String filePath = att.get("file", null);

		if(resourcePath == null && filePath == null)
		{
			throw new XMLLoadException("Neither 'resource' nor 'file' attribute is specified for <includeXml>", node);
		}

		InputStream xmlInput = null;
		
		try
		{
			if(resourcePath != null)
			{
				xmlInput = IncludeXmlNodeHandler.class.getResourceAsStream(resourcePath);
			}
			else
			{
				xmlInput = new FileInputStream(filePath);
			}
			
			XMLBeanParser.parse(xmlInput, parserHandler.getRootBean());
			
			xmlInput.close();
		}catch(Exception ex)
		{
			if(resourcePath != null)
			{
				throw new XMLLoadException("An error occurred while loading xml resource: " + resourcePath, ex, node);
			}
			else
			{
				throw new XMLLoadException("An error occurred while loading xml file: " + filePath, ex, node);
			}
		}

		return null;
	}

	@Override
	public void handleCustomNodeEnd(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att)
	{
	}
}
