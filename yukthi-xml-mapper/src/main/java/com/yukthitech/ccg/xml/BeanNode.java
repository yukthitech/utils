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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.utils.exceptions.InvalidStateException;

public class BeanNode implements Cloneable
{
	public static final String SKIP_NODE_ELEMENT = new String("SKIP_NODE_ELEMENT");
	static final Object NULL_RESERVED_NODE = new Object();

	private String name;
	private String description;
	private Object bean;
	private BeanNode parentNode;
	private String nameSpace;
	private boolean reserved;
	private Class<?> type;
	private Class<?> actualType;
	private XMLAttributeMap attributeMap;
	private String id = null;
	private StringBuffer buff = null;
	private boolean textNode = false;
	
	private List<BeanNode> childNodes;
	
	/**
	 * Context attributes that can be used during node processing.
	 */
	private Map<String, Object> contextAttributes;
	
	/**
	 * Generic type of this node.
	 */
	private Type genericType;
	
	private boolean retainWhiteSpacesEnabled;

	public BeanNode(String nameSpace, String name, IParserHandler parserHandler)
	{
		this(nameSpace, name, null, null, parserHandler);
	}

	public BeanNode(String nameSpace, String name, Object bean, BeanNode parent, IParserHandler parserHandler)
	{
		this.nameSpace = (nameSpace == null || nameSpace.trim().length() == 0) ? null : nameSpace.trim();
		this.reserved = parserHandler.isReserveUri(this.nameSpace);
		this.name = name;
		this.bean = bean;
		this.parentNode = parent;
		this.retainWhiteSpacesEnabled = parserHandler.isRetainWhiteSpacesEnabled();
	}

	private BeanNode(String nameSpace, String name, Object bean, BeanNode parent, boolean reserved, boolean retainWhiteSpacesEnabled)
	{
		this.nameSpace = (nameSpace == null || nameSpace.trim().length() == 0) ? null : nameSpace.trim();
		this.reserved = reserved;
		this.name = name;
		this.bean = bean;
		this.parentNode = parent;
		this.retainWhiteSpacesEnabled = retainWhiteSpacesEnabled;
	}

	void setBean(Object bean)
	{
		if(textNode)
		{
			if(bean instanceof String)
			{
				buff = new StringBuffer((String) bean);
				return;
			}
			
			throw new InvalidStateException("Bean can not be set for text node. [Node: {}, Bean: {}]", this.getNodePath(), bean);
		}

		this.bean = bean;
	}

	public Object getActualBean()
	{
		if(textNode && bean == null)
			bean = getText();

		return bean;
	}
	
	/**
	 * Replaces the current bean with newly specified bean.
	 * This will be helpful in generating dynamic beans.
	 * @param bean
	 */
	public void replaceBean(Object bean)
	{
		this.bean = bean;
	}

	public String getName()
	{
		return name;
	}

	public Object getParent()
	{
		return parentNode.getActualBean();
	}

	public BeanNode getParentNode()
	{
		return parentNode;
	}

	void setParentNode(BeanNode parentNode)
	{
		if(parentNode != null)
		{
			if(parentNode.childNodes == null)
			{
				parentNode.childNodes = new ArrayList<BeanNode>();
			}
		
			parentNode.childNodes.add(this);
		}
		
		this.parentNode = parentNode;
	}
	
	public boolean hasChildNodes()
	{
		return CollectionUtils.isNotEmpty(childNodes);
	}

	public String getNameSpace()
	{
		return nameSpace;
	}

	public Class<?> getType()
	{
		return type;
	}

	public void setType(Class<?> type)
	{
		this.type = type;
	}
	
	public XMLAttributeMap getAttributeMap()
	{
		return attributeMap;
	}

	void setAttributeMap(XMLAttributeMap attributeMap)
	{
		this.attributeMap = attributeMap;
	}

	public String getDescription()
	{
		return description;
	}

	void setDescription(String description)
	{
		this.description = description;
	}

	public String getID()
	{
		return id;
	}

	public void setID(String id)
	{
		this.id = id;
	}

	public void setTextNodeFlag(boolean flag)
	{
		textNode = flag;
		if(textNode == false)
			buff = null;
	}

	public void setText(String text)
	{
		clearText();
		appendText(text);
	}

	void clearText()
	{
		if(buff != null)
			buff.setLength(0);
	}

	public void appendText(String txt)
	{
		if(buff == null)
		{
			if(txt == null || txt.trim().length() == 0)
				return;
			buff = new StringBuffer();
		}

		buff.append(txt);
	}

	public boolean containsText()
	{
		return (buff != null && buff.length() > 0);
	}

	public String getActualText()
	{
		if(buff == null)
			return "";

		return buff.toString();
	}

	public String getText()
	{
		if(buff == null)
			return "";

		/*
		 * StringBuffer res=new StringBuffer(); StringTokenizer st=new
		 * StringTokenizer(buff.toString(),"\n"); String line=null;
		 * 
		 * while(st.hasMoreTokens()) { line=st.nextToken().trim();
		 * if(line.length()==0) continue; res.append(line); res.append("\n"); }
		 */
		if(retainWhiteSpacesEnabled)
		{
			return buff.toString();
		}
		
		return buff.toString().trim();
	}

	public boolean isReserved()
	{
		return reserved;
	}

	public boolean isSkipNode()
	{
		return (description == SKIP_NODE_ELEMENT);
	}

	public boolean isReservedNullNode()
	{
		return (bean == NULL_RESERVED_NODE);
	}

	public boolean isTextNode()
	{
		return textNode;
	}

	public boolean isIDBased()
	{
		return (id != null);
	}

	public String getNodePath()
	{
		BeanNode node = this;
		StringBuffer buff = new StringBuffer();

		while(node != null)
		{
			buff.insert(0, "/");
			buff.insert(0, node);
			node = node.getParentNode();
		}
		return buff.toString();
	}

	public Object clone()
	{
		BeanNode newObj = new BeanNode(nameSpace, name, bean, parentNode, reserved, retainWhiteSpacesEnabled);
		newObj.setDescription(description);
		newObj.setAttributeMap(attributeMap);
		newObj.setType(type);

		return newObj;
	}

	public String toString()
	{
		String description = (this.description == null) ? name : this.description;

		if(nameSpace == null)
			return description;

		return description + "[" + nameSpace + "]";
	}

	public Class<?> getActualType()
	{
		return actualType;
	}

	public void setActualType(Class<?> actualType)
	{
		this.actualType = actualType;
		
		if(type == null)
		{
			this.type = actualType;
		}
	}

	/**
	 * Gets the generic type of this node.
	 *
	 * @return the generic type of this node
	 */
	public Type getGenericType()
	{
		return genericType;
	}

	/**
	 * Sets the generic type of this node.
	 *
	 * @param genericType the new generic type of this node
	 */
	public void setGenericType(Type genericType)
	{
		this.genericType = genericType;
	}
	
	/**
	 * Sets the context attribute with specified name and value.
	 * @param name Name of the attribute to set
	 * @param value value to set
	 */
	public void setContextAttribute(String name, Object value)
	{
		if(contextAttributes == null)
		{
			contextAttributes = new HashMap<String, Object>();
		}
		
		contextAttributes.put(name, value);
	}
	
	/**
	 * Fetches context attribute with specified name.
	 * @param name Name of the attribute to fetch
	 * @return Matching attribute value
	 */
	public Object getContextAttribute(String name)
	{
		if(contextAttributes == null)
		{
			return null;
		}
		
		return contextAttributes.get(name);
	}
}
