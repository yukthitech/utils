package com.yukthitech.autox.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ExecutionLogger;
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
	
	@FreeMarkerMethod(
			description = "Converts specified string into list by splitting it using specified delimiter.",
			returnDescription = "Converted list."
			)
	public static List<String> strToList(
			@FmParam(name = "str", description = "String to be converted") String str,
			@FmParam(name = "delim", description = "Delimiter to be used") String delim
			)
	{
		return new ArrayList<String>( Arrays.asList(str.split(delim)) );
	}

	@FreeMarkerMethod(
			description = "Checks if specified submap is submap of supermap",
			returnDescription = "true if comparision is susccessful."
			)
	public static boolean isSubmap(
			@FmParam(name = "superMap", description = "Super-set map in which submap has to be checked") Map<Object, Object> superMap, 
			@FmParam(name = "superMap", description = "Sub-set map which needs to be checked") Map<Object, Object> submap)
	{
		ExecutionLogger logger = AutomationContext.getInstance().getExecutionLogger();
		
		for(Object key : submap.keySet())
		{
			Object superVal = superMap.get(key);
			Object subVal = submap.get(key);
			
			if(!Objects.equals(superVal, subVal))
			{
				logger.debug("Value from super-map '{}' is not matching value from sub-map '{}' for key: {}", superVal, subVal, key);
				return false;
			}
		}
		
		return true;
	}
	
	@FreeMarkerMethod(
			description = "Evaluates the intersection size of specified collections.",
			returnDescription = "intersection size of collections"
			)
	@SuppressWarnings("unchecked")
	public static int intersectionCount(
			@FmParam(name = "collection1", description = "Collection one to be checked") Collection<Object> collection1, 
			@FmParam(name = "collection2", description = "Collection two to be checked") Collection<Object> collection2
			)
	{
		Collection<Object> intersection = CollectionUtils.intersection(collection1, collection2);
		//return the size
		return intersection.size();
	}
}
