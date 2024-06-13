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
package com.yukthitech.jexpr;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.yukthitech.utils.exceptions.InvalidArgumentException;

public class ParamJsonExprContext extends AbstractMap<String, Object> implements IJsonExprContext
{
	private IJsonExprContext parentContext;
	
	private Map<String, Object> params;
	
	public ParamJsonExprContext(IJsonExprContext parentContext, Map<String, Object> params)
	{
		this.parentContext = parentContext;
		this.params = params == null ? Collections.emptyMap() : params;
	}

	@Override
	public void setValue(String key, Object value)
	{
		if("params".equals(key))
		{
			throw new InvalidArgumentException("Params is read only");
		}
		
		parentContext.setValue(key, value);
	}

	@Override
	public Set<Entry<String, Object>> entrySet()
	{
		return parentContext.entrySet();
	}
	
	@Override
	public Object get(Object key)
	{
		if("params".equals(key))
		{
			return params;
		}
		
		return parentContext.get(key);
	}
	
	
}
