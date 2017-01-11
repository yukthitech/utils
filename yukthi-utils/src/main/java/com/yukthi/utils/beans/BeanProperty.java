package com.yukthi.utils.beans;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import com.yukthi.utils.exceptions.InvalidStateException;

/**
 * Represents property of a bean.
 * @author akiran
 */
public class BeanProperty
{
	/**
	 * Name of the property.
	 */
	private String name;
	
	/**
	 * Type of this property.
	 */
	private Class<?> type;
	
	/**
	 * Property read method.
	 */
	private Method readMethod;
	
	/**
	 * Property write method.
	 */
	private Method writeMethod;
	
	/**
	 * Field corresponding to the property.
	 */
	private Field field;

	/**
	 * Instantiates a new bean property.
	 *
	 * @param name the name
	 * @param readMethod the read method
	 * @param writeMethod the write method
	 * @param field the field
	 */
	public BeanProperty(String name, Class<?> type, Method readMethod, Method writeMethod, Field field)
	{
		this.name = name;
		this.type = type;
		this.readMethod = readMethod;
		this.writeMethod = writeMethod;
		this.field = field;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the read method.
	 *
	 * @return the read method
	 */
	public Method getReadMethod()
	{
		return readMethod;
	}

	/**
	 * Gets the write method.
	 *
	 * @return the write method
	 */
	public Method getWriteMethod()
	{
		return writeMethod;
	}

	/**
	 * Gets the field.
	 *
	 * @return the field
	 */
	public Field getField()
	{
		return field;
	}
	
	/**
	 * Gets the type of this property.
	 *
	 * @return the type of this property
	 */
	public Class<?> getType()
	{
		return type;
	}
	
	/**
	 * Returns true only if the property is read only.
	 *
	 * @return true, if is read only
	 */
	public boolean isReadOnly()
	{
		return (writeMethod == null);
	}
	
	/**
	 * Returns true only if the property is write only.
	 *
	 * @return true, if is write only
	 */
	public boolean isWriteOnly()
	{
		return (readMethod == null);
	}
	
	/**
	 * Gets the current annotation property. The annotation will be checked on read and write methods.
	 * If not found, corresponding field will be checked before returning null.
	 *
	 * @param <A> the generic type
	 * @param annotationType Annotation type to fetch
	 * @return Matching annotation if any.
	 */
	public <A extends Annotation> A getAnnotation(Class<A> annotationType)
	{
		A annotation = null;
		
		//if read method is present and has annotation return the same
		if(readMethod != null)
		{
			annotation = readMethod.getAnnotation(annotationType);
			
			if(annotation != null)
			{
				return annotation;
			}
		}

		//if write method is present and has annotation return the same
		if(writeMethod != null)
		{
			annotation = writeMethod.getAnnotation(annotationType);
			
			if(annotation != null)
			{
				return annotation;
			}
		}
		
		//if field is present and has annotation return the same
		if(field != null)
		{
			annotation = field.getAnnotation(annotationType);
			
			if(annotation != null)
			{
				return annotation;
			}
		}
		
		return null;
	}
	
	/**
	 * Fetches current property value from specified bean.
	 * @param bean
	 * @return
	 */
	public Object getValue(Object bean)
	{
		if(readMethod == null)
		{
			throw new InvalidStateException("Read in invoked on write only property - {}", name);
		}
		
		try
		{
			return readMethod.invoke(bean);
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while fetching bean property '{}' on bean - {}", name, bean);
		}
	}
	
	/**
	 * Loads and returns bean properties.
	 * @param beanType Bean type from which properties should be loaded.
	 * @param readable If true, non readable properties will not be loaded.
	 * @param writeable If true, non writeable properties will not be loaded.
	 * @return List of matching properties.
	 */
	public static List<BeanProperty> loadProperties(Class<?> beanType, boolean readable, boolean writeable)
	{
		PropertyDescriptor propertyDescriptors[] = PropertyUtils.getPropertyDescriptors(beanType);
		
		List<BeanProperty> beanProperties = new ArrayList<BeanProperty>(propertyDescriptors.length);
		Field field = null;
		
		for(PropertyDescriptor prop : propertyDescriptors)
		{
			//ignore getClass method
			if(prop.getReadMethod() != null && "getClass".equals(prop.getReadMethod().getName()))
			{
				continue;
			}
			
			//if readable methods are expected and method is not readable 
			if(readable && prop.getReadMethod() == null)
			{
				continue;
			}

			//if writeable methods are expected and method is not writeable 
			if(writeable && prop.getWriteMethod() == null)
			{
				continue;
			}
			
			try
			{
				field = beanType.getDeclaredField(prop.getName());
			}catch(NoSuchFieldException ex)
			{
				field = null;
			}
			
			beanProperties.add(new BeanProperty(prop.getName(), prop.getPropertyType(), prop.getReadMethod(), prop.getWriteMethod(), field));
		}
		
		return beanProperties;
	}
}
