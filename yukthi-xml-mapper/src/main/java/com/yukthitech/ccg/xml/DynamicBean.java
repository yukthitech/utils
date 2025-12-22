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
package com.yukthitech.ccg.xml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.yukthitech.ccg.xml.util.TypeConversionUtils;
import com.yukthitech.ccg.xml.writer.BeanToDocPopulator;
import com.yukthitech.ccg.xml.writer.IWriteableBean;
import com.yukthitech.ccg.xml.writer.XmlWriterContext;

/**
 * Generic bean that can be used to accept dynamic properties.
 * 
 * @author akiran
 */
public class DynamicBean implements IDynamicNodeAcceptor, IDynamicAttributeAcceptor, IWriteableBean, ITextAcceptor
{
	/**
	 * Name of node creating this bean.
	 */
	private String name;
	
	/**
	 * If this flag enabled, based on prefix values will be type converted.
	 */
	private boolean typeConversationEnabled;

	/**
	 * Map to hold dynamic properties and values.
	 */
	private Map<String, Object> properties = new LinkedHashMap<String, Object>();
	
	private String textContent;
	
	public DynamicBean()
	{
	}

	public DynamicBean(String name, boolean typeConversationEnabled)
	{
		this.name = name;
		this.typeConversationEnabled = typeConversationEnabled;
	}
	
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the if this flag enabled, based on prefix values will be type
	 * converted.
	 *
	 * @param typeConversationEnabled
	 *            the new if this flag enabled, based on prefix values will be
	 *            type converted
	 */
	public void setTypeConversationEnabled(boolean typeConversationEnabled)
	{
		this.typeConversationEnabled = typeConversationEnabled;
	}
	
	public String getTextContent()
	{
		return textContent;
	}

	public void setTextContent(String textContent)
	{
		this.textContent = textContent;
	}

	@Override
	public void set(String propName, String value)
	{
		add(propName, value);
	}
	
	@IgnoreXmlMethod
	public void addList(String name, DynamicBeanList list)
	{
		properties.put(name, list);
	}
	
	public void addDynamic(String name, DynamicBean value)
	{
		this.add(name, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void add(String propName, Object obj)
	{
		if(typeConversationEnabled)
		{
			obj = TypeConversionUtils.strToObject(obj);
		}
		
		if(!properties.containsKey(propName))
		{
			properties.put(propName, obj);
			return;
		}
		
		Object oldVal = properties.get(propName);
		List<Object> valLst = null;
		
		if(oldVal instanceof List)
		{
			valLst = (List<Object>) oldVal;
		}
		else
		{
			valLst = new ArrayList<Object>();
			valLst.add(oldVal);
			
			properties.put(propName, valLst);
		}
		
		valLst.add(obj);
	}

	@Override
	public void add(String propName, String id, Object obj)
	{}

	@Override
	public boolean isIdBased(String arg0)
	{
		return false;
	}

	/**
	 * Gets the map to hold dynamic properties and values.
	 *
	 * @return the map to hold dynamic properties and values
	 */
	public Map<String, Object> getProperties()
	{
		return properties;
	}

	public Object get(String name)
	{
		return properties.get(name);
	}
	
	/**
	 * Converts specified object into simple map or list or simple value.
	 * @param obj object to be converted.
	 * @return converted value.
	 */
	@SuppressWarnings("unchecked")
	private Object toSimpleObject(Object obj)
	{
		if(obj instanceof DynamicBean)
		{
			return ((DynamicBean) obj).toSimpleMap();
		}

		if(obj instanceof List)
		{
			List<Object> newLst = new ArrayList<Object>();
			List<Object> oldLst = (List<Object>) obj;
			
			for(Object lstVal : oldLst)
			{
				if(lstVal instanceof DynamicBean)
				{
					lstVal = ((DynamicBean)lstVal).toSimpleMap();
				}
				
				newLst.add(lstVal);
			}
			
			return newLst;
		}
		
		if(obj instanceof Map)
		{
			Map<Object, Object> finalMap = new LinkedHashMap<Object, Object>();
			Map<Object, Object> oldMap = (Map<Object, Object>) obj;
			
			for(Object key : oldMap.keySet())
			{
				finalMap.put(key, toSimpleObject(oldMap.get(key)));
			}
			
			return finalMap;
		}
		
		return obj;
	}
	
	/**
	 * Converts dynamic bean into simple map.
	 * @return converted simple map
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> toSimpleMap()
	{
		Map<String, Object> finalMap = (Map<String, Object>) toSimpleObject(properties);
		
		if(textContent != null)
		{
			finalMap.put("$textContent", textContent);
		}
		
		return finalMap;
	}
	
	public Map<String, Object> toSimpleMapWithRoot()
	{
		Map<String, Object> finalMap = toSimpleMap();

		if(name != null)
		{
			finalMap = Map.of(name, finalMap);
		}
		
		return finalMap;
	}

	@SuppressWarnings("unchecked")
	private Object convertToSimpleMap(String name, Object object)
	{
		if(object instanceof Map)
		{
			DynamicBean newBean = new DynamicBean(name, typeConversationEnabled);
			newBean.loadSimpleMap((Map<String, Object>) object);
			return newBean;
		}
		else if(object instanceof List)
		{
			List<Object> objLst = (List<Object>) object;
			List<Object> newLst = new ArrayList<Object>(objLst.size());
			
			for(Object lstElem : objLst)
			{
				lstElem = convertToSimpleMap(null, lstElem);
				newLst.add(lstElem);
			}
			
			return newLst;
		}
		
		return object;
	}
	
	/**
	 * Loads the specified simple map into current dynamic bean.
	 * @param map map to be loaded
	 */
	public void loadSimpleMap(Map<String, Object> map)
	{
		for(Map.Entry<String, Object> entry : map.entrySet())
		{
			this.properties.put(entry.getKey(), convertToSimpleMap(entry.getKey(), entry.getValue()));
		}
	}
	
	@SuppressWarnings("unchecked")
	private void write(String name, Object value, XmlWriterContext context, boolean nonAttribute)
	{
		if(typeConversationEnabled)
		{
			value = TypeConversionUtils.objectToStr(value);
		}
		
		if(value instanceof DynamicBean)
		{
			context.startChildElement(name);
			((DynamicBean) value).writeTo(context);
			context.endChildElement();
			return;
		}
		
		if(value instanceof List)
		{
			List<Object> lst = (List<Object>) value;
			
			//create wrapping list element
			context.startChildElement("list");
			context.setAttribute("name", name);
			
			for(Object obj : lst)
			{
				write("element", obj, context, true);
			}
			
			context.endChildElement();
			return;
		}
		
		if(!XMLUtil.isAttributeType(value.getClass()))
		{
			BeanToDocPopulator.populateElement(context.getDocument(), context.getElement(), value, value.getClass(), context.getWriterConfig());
			return;
		}
		
		if(!(value instanceof String))
		{
			value = value.toString();
		}
		
		String strValue = (String) value;
		
		if(strValue.contains("<") || strValue.contains(">") || strValue.contains("&"))
		{
			context.addCdataElement(name, strValue);
		}
		else if(nonAttribute || !context.isAttributable(name) || strValue.length() > 50 || strValue.contains("\n") || strValue.contains("\t"))
		{
			context.addTextElement(name, strValue);
		}
		else
		{
			context.setAttribute(name, strValue);
		}
	}
	
	@Override
	public void writeTo(XmlWriterContext context)
	{
		for(Map.Entry<String, Object> propEntry : this.properties.entrySet())
		{
			Object value = propEntry.getValue();
			write(propEntry.getKey(), value, context, false);
		}
	}
}
