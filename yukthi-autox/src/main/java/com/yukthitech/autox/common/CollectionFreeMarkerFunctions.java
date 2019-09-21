package com.yukthitech.autox.common;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.yukthitech.utils.fmarker.annotaion.FmParam;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

/**
 * Collection related free marker functions.
 * @author akiran
 */
public class CollectionFreeMarkerFunctions
{
	@FreeMarkerMethod(
			description = "Converts specified list into set.",
			returnDescription = "Converted set."
			)
	public static Set<Object> lstToSet(
			@FmParam(name = "list", description = "List to be converted") List<Object> lst)
	{
		if(lst == null)
		{
			return null;
		}
		
		return new HashSet<>(lst);
	}
}
