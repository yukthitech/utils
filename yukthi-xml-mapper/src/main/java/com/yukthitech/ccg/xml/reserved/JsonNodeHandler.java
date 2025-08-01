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

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Locator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.ccg.xml.BeanNode;
import com.yukthitech.ccg.xml.IParserHandler;
import com.yukthitech.ccg.xml.XMLAttributeMap;
import com.yukthitech.ccg.xml.XMLLoadException;
import com.yukthitech.utils.PropertyAccessor;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Reserve node handler to parse json as object.
 * @author akiran
 */
@NodeName(namePattern = "json")
public class JsonNodeHandler implements IReserveNodeHandler
{
	private static Pattern EXT_PATTERN = Pattern.compile("\\s*(\\w+)\\s*\\:\\s*(.*)");
	
	/**
	 * Attribute to fetch property name.
	 */
	private static final String ATTR_ID = "id";
	
	/**
	 * Used to parse json.
	 */
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	@Override
	public Object createCustomNodeBean(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att, Locator locator)
	{
		String name = att.get(ATTR_ID, null);
		
		if(StringUtils.isBlank(name))
		{
			throw new XMLLoadException("Mandatory 'property' attribute not specified.", node, locator);
		}
		
		return null;
	}
	
	private Object loadJson(BeanNode node, Class<?> beanType, Locator locator)
	{
		String text = node.getText().trim();
		Matcher matcher = EXT_PATTERN.matcher(text);
		
		try
		{
			if(matcher.matches())
			{
				String type = matcher.group(1);
				String value = matcher.group(2);
				
				if("res".equals(type))
				{
					text = IOUtils.toString(JsonNodeHandler.class.getResourceAsStream(value));
				}
				else if("file".equals(type))
				{
					text = FileUtils.readFileToString(new File(value));
				}
				else
				{
					throw new InvalidStateException("Invalid json source specified in json content: {}", text);
				}
			}

			return OBJECT_MAPPER.readValue(node.getText(), beanType);
		}catch(Exception ex)
		{
			throw new XMLLoadException("Failed to load json to type: " + beanType.getName(), ex, node, locator);
		}
		
	}

	@Override
	public void handleCustomNodeEnd(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att, Locator locator)
	{
		if(!node.isTextNode())
		{
			throw new XMLLoadException("Non text node is defined as json node", node, locator);
		}
		
		Class<?> beanType = Object.class;
		String clsName = att.get("beanType", null);
		
		if(StringUtils.isNotBlank(clsName))
		{
			try
			{
				beanType = Class.forName(clsName);
			}catch(Exception ex)
			{
				throw new XMLLoadException("Invalid bean type specified for json parsing: " + clsName, ex, node, locator);
			}
		}
		
		Object value = loadJson(node, beanType, locator);
		
		Object parent = node.getParent();
		String name = att.get(ATTR_ID, null);
		
		if(parent == null)
		{
			parserHandler.registerBean(name, value);
			return;
		}
		
		try
		{
			PropertyAccessor.setProperty(parent, name, value);
		}catch(Exception ex)
		{
			throw new XMLLoadException("Failed to set bean of type '" + beanType.getName() + "' as property '" + name + "' on bean of type: " + 
					parent.getClass().getName(), ex, node, locator);
		}
	}
}
