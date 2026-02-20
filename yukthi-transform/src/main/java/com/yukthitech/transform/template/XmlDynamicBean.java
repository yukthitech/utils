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
package com.yukthitech.transform.template;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.ccg.xml.IDynamicAttributeAcceptor;
import com.yukthitech.ccg.xml.IDynamicNodeAcceptor;
import com.yukthitech.ccg.xml.IHybridTextBean;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Generic bean that can be used to accept dynamic properties.
 * 
 * @author akiran
 */
public class XmlDynamicBean implements IDynamicNodeAcceptor, IDynamicAttributeAcceptor, IHybridTextBean
{
	/**
	 * Name of the node used to create this bean.
	 */
	private String name;

	private Map<String, String> attributes = new LinkedHashMap<String, String>();
	
	private Map<String, String> reserveAttributes = new LinkedHashMap<String, String>();
	
	private List<XmlDynamicBean> reserveNodes = new LinkedList<>();

	private List<XmlDynamicBean> nodes = new LinkedList<>();
	
	private String textContent;

	public XmlDynamicBean(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setReserveAttr(String propName, String value)
	{
		reserveAttributes.put(propName, value);
	}

	@Override
	public void set(String propName, String value)
	{
		attributes.put(propName, value);
	}
	
	public void addReserve(String propName, Object obj)
	{
		if(textContent != null)
		{
			throw new InvalidStateException("Both child-nodes and text content is not allowed");
		}
		
		reserveNodes.add((XmlDynamicBean) obj);
	}
	
	@Override
	public void add(String propName, Object obj)
	{
		if(textContent != null)
		{
			throw new InvalidStateException("Both child-nodes and text content is not allowed");
		}

		if(obj instanceof String)
		{
			XmlDynamicBean bean = new XmlDynamicBean(propName);
			bean.setTextContent(obj.toString());
			obj = bean;
		}
		
		nodes.add((XmlDynamicBean) obj);
	}

	@Override
	public void add(String propName, String id, Object obj)
	{}

	@Override
	public boolean isIdBased(String arg0)
	{
		return false;
	}

	public Map<String, String> getReserveAttributes()
	{
		return reserveAttributes;
	}

	public String getReserveAttribute(String name)
	{
		return reserveAttributes.get(name);
	}
	
	public Map<String, String> getAttributes()
	{
		return attributes;
	}

	public List<XmlDynamicBean> getNodes()
	{
		return nodes;
	}
	
	public List<XmlDynamicBean> getReserveNodes()
	{
		return reserveNodes;
	}
	
	public XmlDynamicBean getReserveNode(String name)
	{
		for(XmlDynamicBean node : reserveNodes)
		{
			if(name.equals(node.getName()))
			{
				return node;
			}
		}
		
		return null;
	}

	/**
	 * Gets the value of the reserve node, if not present, checks the attributes.
	 * @param name
	 * @return
	 */
	public Object getReserveValue(String name)
	{
		Object res = getReserveNode(name);

		if(res != null)
		{
			return res;
		}

		return getReserveAttribute(name);
	}
	
	@Override
	public void setText(String text)
	{
		if(StringUtils.isEmpty(text))
		{
			return;
		}
		
		if(!nodes.isEmpty() || !reserveNodes.isEmpty())
		{
			throw new InvalidStateException("Both child-nodes and text content is not allowed");
		}
		
		this.textContent = text.trim();
	}
	
	public void setTextContent(String textContent)
	{
		this.textContent = textContent;
	}
	
	public String getTextContent()
	{
		return textContent;
	}
	
	public boolean isTextNode()
	{
		return StringUtils.isNotBlank(textContent);
	}
}
