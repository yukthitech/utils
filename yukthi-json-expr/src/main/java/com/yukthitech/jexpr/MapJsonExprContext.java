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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Json expr context implementation wrapper over the map.
 * @author Kranthi
 */
public class MapJsonExprContext extends AbstractMap<String, Object> implements IJsonExprContext
{
	private Map<String, Object> mainMap;
	
	public MapJsonExprContext(Map<String, Object> mainMap)
	{
		this.mainMap = mainMap;
	}
	
	public MapJsonExprContext(IJsonExprContext parentContext, String paramKey, Object paramVal)
	{
		mainMap = new HashMap<>();
		
		for(Map.Entry<String, Object> entry : parentContext.entrySet())
		{
			mainMap.put(entry.getKey(), entry.getValue());
		}
		
		mainMap.put(paramKey, paramVal);
	}

	@Override
	public void setValue(String key, Object value)
	{
		mainMap.put(key, value);
	}

	@Override
	public Set<Entry<String, Object>> entrySet()
	{
		return mainMap.entrySet();
	}
	
	@Override
	public Object get(Object key)
	{
		return mainMap.get(key);
	}
	
	@Override
	public boolean containsKey(Object key)
	{
		return mainMap.containsKey(key);
	}
}
