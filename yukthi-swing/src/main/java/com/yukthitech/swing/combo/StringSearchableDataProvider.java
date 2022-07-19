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
