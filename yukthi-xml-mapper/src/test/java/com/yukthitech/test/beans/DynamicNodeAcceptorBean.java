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
package com.yukthitech.test.beans;

import java.util.LinkedHashMap;
import java.util.Map;

import com.yukthitech.ccg.xml.IDynamicAttributeAcceptor;
import com.yukthitech.ccg.xml.IDynamicNodeAcceptor;

/**
 * Mimics FunctionRef-style beans: known setters plus dynamic attribute/node params.
 */
public class DynamicNodeAcceptorBean implements IDynamicAttributeAcceptor, IDynamicNodeAcceptor
{
	private String name;

	private Map<String, Object> params = new LinkedHashMap<String, Object>();

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Map<String, Object> getParams()
	{
		return params;
	}

	@Override
	public void set(String attrName, String value)
	{
		params.put(attrName, value);
	}

	@Override
	public void add(String propName, Object obj)
	{
		params.put(propName, obj);
	}

	@Override
	public void add(String propName, String id, Object obj)
	{
		throw new UnsupportedOperationException("Id-based dynamic nodes are not supported");
	}

	@Override
	public boolean isIdBased(String propName)
	{
		return false;
	}
}
