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
package com.yukthitech.swing.combo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class StringSearchableDataProvider<E> implements ISearchableDataProvider<E>
{
	/**
	 * Full item list.
	 */
	private List<E> items;

	public StringSearchableDataProvider(List<E> items)
	{
		this.items = items;
	}

	@Override
	public List<E> fetchItems(String filter)
	{
		if(StringUtils.isBlank(filter))
		{
			return new ArrayList<>(items);
		}
		
		List<E> filterLst = new ArrayList<>();
		Pattern pattern = buildPattern(filter);
		
		for(E item : this.items)
		{
			String str = toString(item);
		
			if(pattern.matcher(str).matches())
			{
				filterLst.add(item);
			}
		}
		
		return filterLst;
	}
	
	protected String toString(E item)
	{
		return item.toString();
	}
	
	protected Pattern buildPattern(String filter)
	{
		filter = filter.replace("*", ".*");
		filter = filter.replace("?", ".");

		return Pattern.compile(filter);
	}
}
