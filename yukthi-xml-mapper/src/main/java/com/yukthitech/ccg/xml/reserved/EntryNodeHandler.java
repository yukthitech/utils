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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import org.xml.sax.Locator;

import com.yukthitech.ccg.xml.BeanNode;
import com.yukthitech.ccg.xml.DefaultParserHandler;
import com.yukthitech.ccg.xml.IParserHandler;
import com.yukthitech.ccg.xml.XMLAttributeMap;
import com.yukthitech.ccg.xml.XMLLoadException;
import com.yukthitech.ccg.xml.XMLUtil;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Reserve node handler to load entries into parent map.
 * @author akiran
 */
@NodeName(namePattern = "entry")
public class EntryNodeHandler implements IReserveNodeHandler
{
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object createCustomNodeBean(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att, Locator locator)
	{
		Object parent = node.getParent();
		
		if(!(parent instanceof Map))
		{
			throw new XMLLoadException("Reserve node <entry> encountered under non-map node", node, locator);
		}
		
		Type genericType = node.getParentNode().getGenericType();
		Class<?> keyType = Object.class;
		Class<?> valueType = Object.class;

		if(genericType != null && (genericType instanceof ParameterizedType))
		{
			keyType = (Class<?>)((ParameterizedType)genericType).getActualTypeArguments()[0];
			valueType = (Class<?>)((ParameterizedType)genericType).getActualTypeArguments()[1];
		}
		
		if(!XMLUtil.isSupportedAttributeClass(keyType) && !Object.class.equals(keyType))
		{
			throw new XMLLoadException("<entry> reserved node does not support non-attributable key type - " + keyType.getName(), node, locator);
		}
		
		String keyStr = att.get("key", null);
		
		if(keyStr == null)
		{
			throw new XMLLoadException("No 'key' attribute specified for <entry> node", node, locator);
		}
		
		//parse the key 
		Object key = XMLUtil.parseAttributeObject(keyStr, keyType, parserHandler.getDateFormat());
		node.getAttributeMap().removeAttribute("key");
		
		if(att.containsReservedKey(DefaultParserHandler.ATTR_BEAN_TYPE))
		{
			String beanType = att.getReserved(DefaultParserHandler.ATTR_BEAN_TYPE, null);
			
			try
			{
				valueType = Class.forName(beanType);
			}catch(Exception ex)
			{
				throw new InvalidStateException("Invalid bean type specified: {}", beanType);
			}
		}

		node.setType(valueType);
		node.setActualType(valueType);

		//if number of attributes is just key and the value type can be supported as node text
		if(node.getAttributeMap().size() <= 1 && (XMLUtil.isSupportedAttributeClass(valueType) || Object.class.equals(valueType)) )
		{
			//expect the value to be provided as node text
			
			//set key on context so that it can be used during end node processing
			node.setContextAttribute("key", key);
			
			node.setTextNodeFlag(true);
			return null;
		}

		Object result = parserHandler.createBean(node, att);
		((Map) parent).put(key, result);
		
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void handleCustomNodeEnd(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att, Locator locator)
	{
		if(!node.isTextNode())
		{
			return;
		}
		
		Object value = parserHandler.parseTextNodeValue(node, att);
		Map<Object, Object> parent = (Map) node.getParent();
		
		parent.put(node.getContextAttribute("key"), value);
	}
}
