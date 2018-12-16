package com.yukthitech.utils.beans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yukthitech.utils.annotations.PropertyAdder;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Represents property of a bean.
 * @author akiran
 */
public class BeanProperty
{
	/**
	 * Setter method pattern.
	 */
	private static final Pattern SETTER_PATTERN = Pattern.compile("set([\\w\\$]+)");
	
	/**
	 * Adder method pattern.
	 */
	private static final Pattern ADDER_PATTERN = Pattern.compile("add([\\w\\$]+)");
	
	/**
	 * Getter method pattern.
	 */
	private static final Pattern GETTER_PATTERN = Pattern.compile("get([\\w\\$]+)");
	
	/**
	 * Is method pattern.
	 */
	private static final Pattern IS_PATTERN = Pattern.compile("is([\\w\\$]+)");
	
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
	 * Add method based on type of property.
	 */
	private Method addMethod;
	
	/**
	 * Property name of the adder.
	 */
	private String adderName;
	
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
	 * Instantiates a new bean property.
	 *
	 * @param name the name
	 */
	private BeanProperty(String name)
	{
		this.name = name;
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
	 * Gets the add method based on type of property.
	 *
	 * @return the add method based on type of property
	 */
	public Method getAddMethod()
	{
		return addMethod;
	}

	/**
	 * Sets the add method based on type of property.
	 *
	 * @param addMethod the new add method based on type of property
	 */
	public void setAddMethod(Method addMethod)
	{
		this.addMethod = addMethod;
	}
	
	/**
	 * Gets the property name of the adder.
	 *
	 * @return the property name of the adder
	 */
	public String getAdderName()
	{
		if(adderName != null)
		{
			return adderName;
		}
		
		if(addMethod == null)
		{
			return null;
		}
		
		String adderName = addMethod.getName();
		adderName = adderName.substring(3);
		adderName = Character.toUpperCase(adderName.charAt(0)) + adderName.substring(1);
		
		this.adderName = adderName;
		return adderName;
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
			throw new InvalidStateException("Read is invoked on write only property - {}", name);
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
	 * Sets the current property on specified bean with specified value.
	 * @param bean bean on which property needs to be set
	 * @param value value needs to be set
	 */
	public void setValue(Object bean, Object value)
	{
		if(writeMethod == null)
		{
			throw new InvalidStateException("Read is invoked on write only property - {}", name);
		}
		
		try
		{
			writeMethod.invoke(bean, value);
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while setting bean property '{}' on bean '{}' with value - {}", name, bean, value);
		}
	}
	
	public static List<BeanProperty> loadProperties(Class<?> beanType, boolean readable, boolean writeable)
	{
		return loadProperties(beanType, readable, writeable, false);
	}
	
	/**
	 * Loads and returns bean properties.
	 * @param beanType Bean type from which properties should be loaded.
	 * @param readable If true, non readable properties will not be loaded.
	 * @param writeable If true, non writeable properties will not be loaded.
	 * @param linkAdders if true, this method will try to identify approp adders also (based on param type) and set it on properties.
	 * @return List of matching properties.
	 */
	public static List<BeanProperty> loadProperties(Class<?> beanType, boolean readable, boolean writeable, boolean linkAdders)
	{
		List<BeanProperty> propLst = fetchProperties(beanType, linkAdders);
		List<BeanProperty> resLst = new ArrayList<BeanProperty>(propLst.size());
		
		for(BeanProperty prop : propLst)
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

			resLst.add(prop);
		}
		
		return resLst;
	}
	
	/**
	 * Parses the method name and checks if setter or getter and populated bean property map approp.
	 * @param method method to be inspected
	 * @param propMap prop map to be populated
	 */
	private static BeanProperty fetchPropertyDetails(Method method, Map<String, BeanProperty> propMap)
	{
		boolean setter = true;
		Matcher matcher = SETTER_PATTERN.matcher(method.getName());
		Class<?> type = null;
		
		//if setter is not found check for adder
		if(!matcher.matches())
		{
			matcher = ADDER_PATTERN.matcher(method.getName());
		}
		
		//if not setter method
		if(!matcher.matches())
		{
			//before checking for getter pattern, ensure zero params
			if(method.getParameterTypes().length > 0 || void.class.equals(method.getReturnType()))
			{
				return null;
			}
			
			setter = false;
			
			//check if getter
			matcher = GETTER_PATTERN.matcher(method.getName());

			if(!matcher.matches())
			{
				if(!boolean.class.equals(method.getReturnType()))
				{
					return null;
				}
				
				matcher = IS_PATTERN.matcher(method.getName());
				
				if(!matcher.matches())
				{
					return null;
				}
			}
			
			type = method.getReturnType();
		}
		//if setter ensure single param
		else 
		{
			if(method.getParameterTypes().length != 1)
			{
				return null;
			}
			
			type = method.getParameterTypes()[0];
		}

		String propName = matcher.group(1);
		//make first char of prop name to lower
		propName = propName.substring(0, 1).toLowerCase() + propName.substring(1);
		
		BeanProperty beanProperty = propMap.get(propName);
		
		if(beanProperty == null)
		{
			beanProperty = new BeanProperty(propName);
			beanProperty.type = type;
			
			try
			{
				Field field = method.getDeclaringClass().getDeclaredField(propName);
				beanProperty.field = field;
			}catch(NoSuchFieldException ex)
			{
				//ignore
			}
			
			propMap.put(propName, beanProperty);
		}
		//for existing property ensure type is matching
		else
		{
			if(!type.equals(beanProperty.type))
			{
				return null;
			}
		}
		
		if(setter)
		{
			beanProperty.writeMethod = method;
		}
		else
		{
			beanProperty.readMethod = method;
		}
		
		return beanProperty;
	}
	
	/**
	 * Fetches properties for given type.
	 * @param beanType type for which properties needs to be fetched.
	 * @param linkAdders if true, will try to add adder to properties (based on type).
	 * @return properties
	 */
	private static List<BeanProperty> fetchProperties(Class<?> beanType, boolean linkAdders)
	{
		Method methods[] = beanType.getMethods();
		Map<String, BeanProperty> propMap = new TreeMap<String, BeanProperty>();
		BeanProperty prop = null;
		
		for(Method method : methods)
		{
			prop = fetchPropertyDetails(method, propMap);
			
			if(prop == null)
			{
				continue;
			}
			
			if(linkAdders && prop.getAddMethod() == null)
			{
				PropertyAdder propAdder = prop.getAnnotation(PropertyAdder.class);
				
				if(propAdder != null)
				{
					Method addMethod = null;
					
					try
					{
						addMethod = beanType.getMethod(propAdder.value(), prop.getType());
					}catch(Exception ex)
					{
					}
					
					if(addMethod != null)
					{
						prop.setAddMethod(addMethod);
					}
				}
			}
		}
		
		return new ArrayList<BeanProperty>(propMap.values());
	}
}
