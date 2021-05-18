package com.yukthitech.persistence.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
		//check if instance of collection can be created directly from collection type
		try
		{
			return (Collection) collectionType.newInstance();
		}catch(Exception ex)
		{
		}

		//if not use abstraction types and determine the type to be used
		if(List.class.isAssignableFrom(collectionType))
		{
			collectionType = ArrayList.class;
		}
		else if(Set.class.isAssignableFrom(collectionType))
		{
			collectionType = HashSet.class;
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
