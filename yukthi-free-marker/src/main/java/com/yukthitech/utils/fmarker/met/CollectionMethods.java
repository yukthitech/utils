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
package com.yukthitech.utils.fmarker.met;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.yukthitech.utils.annotations.Named;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;
import com.yukthitech.utils.fmarker.annotaion.ExampleDoc;
import com.yukthitech.utils.fmarker.annotaion.FmParam;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

/**
 * Default collection methods.
 * @author Kranthi
 */
@Named("Collection Methods")
public class CollectionMethods
{
	/**
	 * Represents encapsulation of grouping based on key.
	 * @author Kranthi
	 */
	public static class Group
	{
		/**
		 * Key based on which this group is created.
		 */
		private Object key;
		
		/**
		 * Elements of this group having same key.
		 */
		private List<Object> elements = new LinkedList<>();
		
		public Group(Object key)
		{
			this.key = key;
		}
		
		public Group(Object key, List<Object> elements)
		{
			this.key = key;
			this.elements = elements;
		}

		/**
		 * Gets the key based on which this group is created.
		 *
		 * @return the key based on which this group is created
		 */
		public Object getKey()
		{
			return key;
		}

		/**
		 * Sets the key based on which this group is created.
		 *
		 * @param key the new key based on which this group is created
		 */
		public void setKey(Object key)
		{
			this.key = key;
		}

		/**
		 * Gets the elements of this group having same key.
		 *
		 * @return the elements of this group having same key
		 */
		public List<Object> getElements()
		{
			return elements;
		}

		/**
		 * Sets the elements of this group having same key.
		 *
		 * @param elements the new elements of this group having same key
		 */
		public void setElements(List<Object> elements)
		{
			this.elements = elements;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if(obj == this)
			{
				return true;
			}

			if(!(obj instanceof CollectionMethods.Group))
			{
				return false;
			}

			CollectionMethods.Group other = (CollectionMethods.Group) obj;
			return Objects.equals(key, other.key) && Objects.equals(elements, other.elements);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashcode()
		 */
		@Override
		public int hashCode()
		{
			return Objects.hash(key, elements);
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			StringBuilder builder = new StringBuilder(super.toString());
			builder.append("[");

			builder.append("Key: ").append(key);
			builder.append(",").append("Elements: ").append(elements);

			builder.append("]");
			return builder.toString();
		}

	}
	
	@FreeMarkerMethod(
			description = "Groups elements of specified collection based on specified keyExpression",
			returnDescription = "List of groups. Each group has key (value of key based on which current group is created) and elements having same key.")
	public static List<Group> groupBy(
			@FmParam(name = "collection", description = "Collection of objects which needs grouping") 
			Collection<Object> collection, 
			
			@FmParam(name = "keyExpression", description = "Freemarker key expression which will be executed on each of collection element. "
					+ "And obtained key will be used for grouping.") 
			String keyExpression)
	{
		if(collection == null || collection.isEmpty())
		{
			return Collections.emptyList();
		}
		
		Map<Object, Group> groups = new LinkedHashMap<>();
		
		for(Object value : collection)
		{
			Object key = FreeMarkerEngine.getCurrentInstance().fetchValue("Key-Expression", keyExpression, value);
			Group grp = groups.get(key);
			
			if(grp == null)
			{
				grp = new Group(key);
				groups.put(key, grp);
			}
			
			grp.elements.add(value);
		}
		
		return new LinkedList<>(groups.values());
	}

	@FreeMarkerMethod(
			description = "Sorted elements of specified collection based on specified keyExpression. "
					+ "Duplicate elements (with same key) will be kept together (though internal order is not guaranteed).",
			returnDescription = "List of ordered elements based on specified key expression.")
	public static List<Object> sortBy(
			@FmParam(name = "collection", description = "Collection of objects which needs sorting") 
			Collection<Object> collection, 
			
			@FmParam(name = "keyExpression", description = "Freemarker key expression which will be executed on each of collection element. "
					+ "And obtained key will be used for sorting.") 
			String keyExpression)
	{
		if(collection == null || collection.isEmpty())
		{
			return Collections.emptyList();
		}
		
		Map<Object, Group> groups = new TreeMap<>();
		
		for(Object value : collection)
		{
			Object key = FreeMarkerEngine.getCurrentInstance().fetchValue("Key-Expression", keyExpression, value);
			Group grp = groups.get(key);
			
			if(grp == null)
			{
				grp = new Group(key);
				groups.put(key, grp);
			}
			
			grp.elements.add(value);
		}
		
		LinkedList<Object> res = new LinkedList<>();
		
		for(Group grp : groups.values())
		{
			res.addAll(grp.elements);
		}
		
		return res;
	}

	@FreeMarkerMethod(
			description = "Extracts and returns the values collection as list of specified map.",
			returnDescription = "the values collection of specified map.")
	public static Collection<Object> mapValues(
			@FmParam(name = "map", description = "Map whose values has to be extracted") 
			Map<Object, Object> map)
	{
		return new ArrayList<>(map.values());
	}
	
	@FreeMarkerMethod(
			description = "Extracts and returns the keys collection as list of specified map.",
			returnDescription = "the values collection of specified map.")
	public static Collection<Object> mapKeys(
			@FmParam(name = "map", description = "Map whose keys has to be extracted") 
			Map<Object, Object> map)
	{
		return new ArrayList<>(map.keySet());
	}

	/**
	 * Converts collection of objects into string.
	 * @param lst list of objects to be converted
	 * @param prefix prefix to be used at the starting.
	 * @param delimiter Delimiter to be used between elements.
	 * @param suffix Suffix to be used at end of string.
	 * @param emptyString String that will be returned if input list is null or empty.
	 * @return result string.
	 */
	@FreeMarkerMethod(
			description = "Converts collection of objects into string.",
			returnDescription = "Converted string",
			examples = {
				@ExampleDoc(usage = "collectionToString(lst, '[', ' | ', ']', '')", result = "[a | b | c]"),
				@ExampleDoc(usage = "collectionToString(null, '[', ' | ', ']', '<empty>')", result = "<empty>")
			})
	public static String collectionToString(
			@FmParam(name = "lst", description = "Collection to be converted") Collection<Object> lst, 
			@FmParam(name = "prefix", description = "Prefix to be used at start of coverted string.", defaultValue = "empty string") String prefix, 
			@FmParam(name = "delimiter", description = "Delimiter to be used between the collection elements.", defaultValue = "comma (,)") String delimiter, 
			@FmParam(name = "suffix", description = "Suffix to be used at end of converted string.", defaultValue = "empty string") String suffix, 
			@FmParam(name = "emptyString", description = "String to be used when input list is null or empty.", defaultValue = "empty string") String emptyString)
	{
		emptyString = (emptyString == null) ? "" : emptyString;
		
		if(lst == null || lst.isEmpty())
		{
			return emptyString;
		}
		
		prefix = (prefix == null) ? "" : prefix;
		delimiter = (delimiter == null) ? "," : delimiter;
		suffix = (suffix == null) ? "" : suffix;

		StringBuilder builder = new StringBuilder(prefix);
		boolean first = true;
		
		for(Object elem : lst)
		{
			if(!first)
			{
				builder.append(delimiter);
			}
			
			builder.append(elem);
			first = false;
		}
		
		builder.append(suffix);
		return builder.toString();
	}

	/**
	 * Converts map of objects into string.
	 * @param map map of objects to be converted
	 * @param template Template representing how key and value should be converted into string (the string can have #key and #value which will act as place holders)
	 * @param prefix prefix to be used at the starting.
	 * @param delimiter Delimiter to be used between elements.
	 * @param suffix Suffix to be used at end of string.
	 * @param emptyString String that will be returned if input list is null or empty.
	 * @return result string.
	 */
	@FreeMarkerMethod(
			description = "Converts map of objects into string.",
			returnDescription = "Converted string",
			examples = {
				@ExampleDoc(usage = "mapToString(map, '#key=#value', '[', ' | ', ']', '')", result = "[a=1 | b=2 | c=3]"),
				@ExampleDoc(usage = "mapToString(null, '#key=#value', '[', ' | ', ']', '<empty>')", result = "<empty>")
			})
	public static String mapToString(
			@FmParam(name = "map", description = "Prefix to be used at start of coverted string") Map<Object, Object> map, 
			@FmParam(name = "template", description = "Template representing how key and value should be converted "
					+ "into string (the string can have #key and #value which will act as place holders).", defaultValue = "#key=#value") String template, 
			@FmParam(name = "prefix", description = "Prefix to be used at start of coverted string.", defaultValue = "empty string") String prefix, 
			@FmParam(name = "delimiter", description = "Delimiter to be used between elements.", defaultValue = "comma (,)") String delimiter, 
			@FmParam(name = "suffix", description = "Suffix to be used at end of string.", defaultValue = "empty string") String suffix, 
			@FmParam(name = "emptyString", description = "String that will be returned if input map is null or empty.", defaultValue = "empty string") String emptyString)
	{
		emptyString = (emptyString == null) ? "" : emptyString;

		if(map == null || map.isEmpty())
		{
			return emptyString;
		}
		
		template = (template == null) ? "#key=#value" : template;
		prefix = (prefix == null) ? "" : prefix;
		delimiter = (delimiter == null) ? "," : delimiter;
		suffix = (suffix == null) ? "" : suffix;

		StringBuilder builder = new StringBuilder(prefix);
		boolean first = true;
		
		for(Entry<Object, Object> entry : map.entrySet())
		{
			if(!first)
			{
				builder.append(delimiter);
			}
			
			builder.append( template.replace("#key", "" + entry.getKey()).replace("#value", "" + entry.getValue()) );
			first = false;
		}
		
		builder.append(suffix);
		return builder.toString();
	}
	
	@FreeMarkerMethod(
			description = "Checks if the specified collection contains the specified value.",
			returnDescription = "true if the specified collection contains the specified value, false otherwise.")
	public static boolean contains(
			@FmParam(name = "collection", description = "Collection to be checked") Collection<Object> collection, 
			@FmParam(name = "value", description = "Value to be checked") Object value)
	{
		return collection.contains(value);
	}
}
