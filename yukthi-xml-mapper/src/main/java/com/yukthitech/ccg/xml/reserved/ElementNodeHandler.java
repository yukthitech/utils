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
import java.util.Collection;

import org.xml.sax.Locator;

import com.yukthitech.ccg.xml.BeanNode;
import com.yukthitech.ccg.xml.DefaultParserHandler;
import com.yukthitech.ccg.xml.IParserHandler;
import com.yukthitech.ccg.xml.XMLAttributeMap;
import com.yukthitech.ccg.xml.XMLUtil;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Reserve node handler to add elements to parent collection.
 * @author akiran
 */
@NodeName(namePattern = "element")
public class ElementNodeHandler implements IReserveNodeHandler
{
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object createCustomNodeBean(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att, Locator locator)
	{
		Object parent = node.getParent();
		
		if(!(parent instanceof Collection))
		{
			throw new InvalidStateException("Reserve node <element> encountered under non-collection node");
		}
		
		Type genericType = node.getParentNode().getGenericType();
		Class<?> elementType = null;
		
		if(genericType == null || !(genericType instanceof ParameterizedType))
		{
			elementType = String.class;
		}
		else
		{
			elementType = (Class<?>)((ParameterizedType)genericType).getActualTypeArguments()[0];			
		}
		
		if(att.containsReservedKey(DefaultParserHandler.ATTR_BEAN_TYPE))
		{
			String beanType = att.getReserved(DefaultParserHandler.ATTR_BEAN_TYPE, null);
			
			try
			{
				elementType = Class.forName(beanType);
			}catch(Exception ex)
			{
				throw new InvalidStateException("Invalid bean type specified: {}", beanType);
			}
		}
		
		node.setType(elementType);
		node.setActualType(elementType);
		
		if(XMLUtil.isSupportedAttributeClass(elementType))
		{
			node.setTextNodeFlag(true);
			return null;
		}
		
		Object result = parserHandler.createBean(node, att);
		((Collection) parent).add(result);
		
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
		Collection<Object> parent = (Collection) node.getParent();
		
		parent.add(value);
	}
}
