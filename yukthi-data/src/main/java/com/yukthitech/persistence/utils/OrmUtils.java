package com.yukthitech.persistence.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Orm common utils.
 * @author akiran
 */
public class OrmUtils
{
	/**
	 * Creates collection of specified type.
	 * @param collectionType
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Collection<Object> createCollection(Class<?> collectionType)
	{
		//if target collection is list type
		if(collectionType.isAssignableFrom(ArrayList.class))
		{
			collectionType = ArrayList.class;
		}
		//if target collection is set type
		else if(collectionType.isAssignableFrom(HashSet.class))
		{
			collectionType = HashSet.class;
		}
		//if target collection is sorted set type
		else if(collectionType.isAssignableFrom(TreeSet.class))
		{
			collectionType = TreeSet.class;
		}
		
		try
		{
			return (Collection) collectionType.newInstance();
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while creating collection of type: " + collectionType.getName(), ex);
		}
	}

}
