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
package com.yukthitech.ccg.xml.writer;

import static com.yukthitech.ccg.xml.writer.XmlBeanWriter.CCG_PREFIX;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.yukthitech.ccg.xml.DefaultParserHandler;
import com.yukthitech.ccg.xml.XMLUtil;
import com.yukthitech.ccg.xml.annotations.CollectionElement;
import com.yukthitech.ccg.xml.annotations.XmlAttribute;
import com.yukthitech.ccg.xml.annotations.XmlElement;
import com.yukthitech.ccg.xml.annotations.XmlIgnore;
import com.yukthitech.utils.PropertyAccessor.Property;

/**
 * Populates data from bean to document.
 * @author akiran
 */
public class BeanToDocPopulator
{
	@SuppressWarnings("rawtypes")
	private static String toAttributeString(Object value)
	{
		if(value instanceof Date)
		{
			return XmlBeanWriter.DEF_DATE_FORMAT.format(value);
		}
		
		if(value instanceof Class)
		{
			return ((Class) value).getName();
		}
		
		return value.toString();
	}
	
	/**
	 * Adds attribute property to specified element.
	 * @param document
	 * @param element
	 * @param value
	 * @param prop
	 */
	private static void addAttributableProperty(Document document, Element element, Object value, Property prop)
	{
		XmlAttribute attr = prop.getAnnotation(XmlAttribute.class);
		String valueStr = toAttributeString(value);
		
		if(attr != null)
		{
			String name = StringUtils.isBlank(attr.name()) ? prop.getName() : attr.name();
			element.setAttribute(name, valueStr);
			return;
		}
		
		XmlElement elem = prop.getAnnotation(XmlElement.class);
		String name = ( elem == null || StringUtils.isBlank(elem.name()) ) ? prop.getName() : elem.name();
		
		Element propElem = document.createElement(name);
		element.appendChild(propElem);
		
		if(elem != null && elem.cdata())
		{
			propElem.appendChild( document.createCDATASection(valueStr) );
		}
		else
		{
			propElem.appendChild( document.createTextNode(valueStr) );
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void populateCollectionNode(Document document, Element lstElem, Object value, Type genericType, XmlWriterConfig writerConfig, String elementName)
	{
		//fetch element type
		Type elementType = null;
		
		if((genericType instanceof ParameterizedType))
		{
			elementType = ((ParameterizedType)genericType).getActualTypeArguments()[0];			
		}
		
		Collection<Object> lst = (Collection) value;
		Element elemElem = null; 
				
		for(Object elemBean : lst)
		{
			if(elementName != null)
			{
				elemElem = document.createElement(elementName);
			}
			else
			{
				elemElem = document.createElement(CCG_PREFIX + "element");
			}
		
			lstElem.appendChild(elemElem);
			
			if(elementType != null && XMLUtil.isAttributeType((Class<?>)elementType))
			{
				String valueStr = toAttributeString(elemBean);
				elemElem.appendChild(document.createTextNode(valueStr));
			}
			else
			{
				populateElement(document, elemElem, elemBean, elementType, writerConfig);
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void populateMapNode(Document document, Element mapElem, Object value, Type genericType, XmlWriterConfig writerConfig)
	{
		//fetch element type
		Type valueType = Object.class;

		if(genericType != null && (genericType instanceof ParameterizedType))
		{
			valueType = ((ParameterizedType)genericType).getActualTypeArguments()[1];
		}
		
		Map<Object, Object> map = (Map) value;
		Element entryElem = null; 
				
		for(Object key : map.keySet())
		{
			Object mapValue = map.get(key);
			
			entryElem = document.createElement(CCG_PREFIX + "entry");
			mapElem.appendChild(entryElem);
			
			entryElem.setAttribute("key", toAttributeString(key));
			
			if(valueType != null && XMLUtil.isAttributeType((Class<?>)valueType))
			{
				String valueStr = toAttributeString(mapValue);
				entryElem.appendChild(document.createTextNode(valueStr));
			}
			else
			{
				populateElement(document, entryElem, mapValue, valueType, writerConfig);
			}
		}
	}

	/**
	 * Creates sub node element.
	 * @param document
	 * @param parentElem
	 * @param value
	 * @param prop
	 */
	private static void createSubnode(Document document, Element parentElem, Object value, Property prop, XmlWriterConfig writerConfig)
	{
		XmlElement elem = prop.getAnnotation(XmlElement.class);
		String name = ( elem == null || StringUtils.isBlank(elem.name()) ) ? prop.getName() : elem.name();
		
		if(value instanceof Collection)
		{
			CollectionElement colElem = prop.getAnnotation(CollectionElement.class);
			
			if(colElem != null)
			{
				populateCollectionNode(document, parentElem, value, prop.getGetter().getGenericReturnType(), writerConfig, colElem.value());
				return;
			}
		}
		
		Element newElem = document.createElement(name);
		parentElem.appendChild(newElem);
		
		populateElement(document, newElem, value, prop.getGetter().getGenericReturnType(), writerConfig);
	}
	
	/**
	 * Adds attributes and sub nodes recursively to specified document element.
	 * @param document
	 * @param element
	 * @param bean
	 */
	public static void populateElement(Document document, Element element, Object bean, Type declaredType, XmlWriterConfig writerConfig)
	{
		Class<?> rawDeclaredType = null;
		
		if(declaredType instanceof Class)
		{
			rawDeclaredType = (Class<?>) declaredType;
		}
		else if(declaredType instanceof ParameterizedType)
		{
			rawDeclaredType = (Class<?>) ((ParameterizedType) declaredType).getRawType();
		}
		
		if(rawDeclaredType != null && 
				!(bean instanceof Collection) &&
				!(bean instanceof Map) &&
				!rawDeclaredType.equals(bean.getClass()))
		{
			element.setAttribute(CCG_PREFIX + DefaultParserHandler.ATTR_BEAN_TYPE, bean.getClass().getName());
		}
		
		if(bean instanceof Collection)
		{
			populateCollectionNode(document, element, bean, declaredType, writerConfig, null);
			return;
		}
		
		if(bean instanceof Map)
		{
			populateMapNode(document, element, bean, declaredType, writerConfig);
			return;
		}
		
		if(XMLUtil.isAttributeType(bean.getClass()) || bean.getClass().isEnum())
		{
			element.appendChild(document.createTextNode(toAttributeString(bean)));
			return;
		}
		
		if(bean instanceof IWriteableBean)
		{
			((IWriteableBean) bean).writeTo(new XmlWriterContext(element, document, writerConfig));
			return;
		}

		List<Property> properties = XmlBeanWriter.getReadProperties(bean.getClass(), writerConfig);
		Object value = null;
		
		for(Property prop : properties)
		{
			//skip property which is marked with @XmlIgnore
			if(prop.getAnnotation(XmlIgnore.class) != null)
			{
				continue;
			}
			
			value = prop.getValue(bean);
			
			if(value == null)
			{
				continue;
			}
			
			if(XMLUtil.isAttributeType(prop.getType()))
			{
				addAttributableProperty(document, element, value, prop);
				continue;
			}
			
			createSubnode(document, element, value, prop, writerConfig);
		}
	}
}
