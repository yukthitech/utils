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
package com.yukthitech.utils.fmarker;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import com.yukthitech.utils.fmarker.annotaion.FmParam;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

/**
 * Default collection methods.
 * @author Kranthi
 */
public class DefaultCollectionMethods
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

			if(!(obj instanceof DefaultCollectionMethods.Group))
			{
				return false;
			}

			DefaultCollectionMethods.Group other = (DefaultCollectionMethods.Group) obj;
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
}
