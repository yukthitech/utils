package com.yukthitech.automation.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Encapsulation of a bean, and override properties.
 * @author akiran
 */
public class ObjectCopy implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static class Property implements Serializable
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Name or nested property.
		 */
		private String name;
		
		/**
		 * Value for the property.
		 */
		private String value;

		public void setName(String name)
		{
			this.name = name;
		}

		public void setValue(String value)
		{
			this.value = value;
		}
	}
	
	/**
	 * Base object to be used.
	 */
	private Object baseObject;
	
	/**
	 * Properties to override on base object copy.
	 */
	private List<Property> properties = new ArrayList<>();

	public void setBaseObject(Object baseObject)
	{
		this.baseObject = baseObject;
	}

	/**
	 * Adds property to override on bean.
	 * @param property property
	 */
	public void addProperty(Property property)
	{
		this.properties.add(property);
	}
	
	/**
	 * Creates copy of base object and override specified properties.
	 * @return base object copy with overridden properties.
	 */
	public Object createCopy()
	{
		try
		{
			Object copy = baseObject.getClass().newInstance();
			BeanUtils.copyProperties(copy, baseObject);
			
			for(Property prop : this.properties)
			{
				BeanUtils.setProperty(copy, prop.name, prop.value);
			}
			
			return copy;
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while creating copy of object: [}", baseObject, ex);
		}
	}
}
