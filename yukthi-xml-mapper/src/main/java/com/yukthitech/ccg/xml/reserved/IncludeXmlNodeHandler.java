/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.ccg.xml.reserved;

import java.io.FileInputStream;
import java.io.InputStream;

import org.xml.sax.Locator;

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
	public Object createCustomNodeBean(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att, Locator locator)
	{
		String resourcePath = att.get("resource", null);
		String filePath = att.get("file", null);

		if(resourcePath == null && filePath == null)
		{
			throw new XMLLoadException("Neither 'resource' nor 'file' attribute is specified for <includeXml>", node, locator);
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
			
			XMLBeanParser.parse(xmlInput, parserHandler.getRootBean(), parserHandler);
			
			xmlInput.close();
		}catch(Exception ex)
		{
			if(resourcePath != null)
			{
				throw new XMLLoadException("An error occurred while loading xml resource: " + resourcePath, ex, node, locator);
			}
			else
			{
				throw new XMLLoadException("An error occurred while loading xml file: " + filePath, ex, node, locator);
			}
		}

		return null;
	}

	@Override
	public void handleCustomNodeEnd(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att, Locator locator)
	{
	}
}
