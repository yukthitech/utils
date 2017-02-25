package com.yukthitech.indexer.es;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IndexUtils
{
	@SuppressWarnings("rawtypes")
	public static Object toLowerCase(Object value)
	{
		if(value instanceof String)
		{
			value = ((String)value).toLowerCase();
		}
		else if(value instanceof Collection)
		{
			List<String> lst = new ArrayList<>();
			
			for(Object element : (Collection)value)
			{
				lst.add(((String)element).toLowerCase());
			}
			
			value = lst;
		}

		return value;
	}
}
