/**
 * 
 */
package com.yukthi.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthi.utils.exceptions.InvalidStateException;

/**
 * Contains common utility methods
 * 
 * @author akiran
 */
public class CommonUtils
{
	private static Logger logger = LogManager.getLogger(CommonUtils.class);

	private static Map<Class<?>, Class<?>> wrapperToPrimitive = new HashMap<Class<?>, Class<?>>();
	private static Map<Class<?>, Class<?>> primitiveToWrapper = new HashMap<Class<?>, Class<?>>();
	private static Map<String, Class<?>> primitiveNameToClass = new HashMap<String, Class<?>>();

	static
	{
		addMapping(Boolean.class, boolean.class);
		addMapping(Byte.class, byte.class);
		addMapping(Character.class, char.class);
		addMapping(Short.class, short.class);
		addMapping(Integer.class, int.class);
		addMapping(Long.class, long.class);
		addMapping(Float.class, float.class);
		addMapping(Double.class, double.class);
	}

	private static void addMapping(Class<?> wrapperType, Class<?> primType)
	{
		wrapperToPrimitive.put(wrapperType, primType);
		primitiveToWrapper.put(primType, wrapperType);
		primitiveNameToClass.put(primType.getName(), primType);
	}

	private static final Pattern EXPRESSION_PATTERN = Pattern.compile("\\$\\{([\\w\\.\\(\\)\\=]+)\\}");
	private static final Pattern DEF_VALUE_PATTERN = Pattern.compile("([\\w\\.\\(\\)]+)=([\\w\\.\\(\\)]*)");

	/**
	 * Returns true if specified type is primitive wrapper type
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isWrapperClass(Class<?> type)
	{
		return wrapperToPrimitive.containsKey(type);
	}

	/**
	 * Gets wrapper type for specified primitive type
	 * 
	 * @param primitiveType
	 * @return
	 */
	public static Class<?> getWrapperType(Class<?> primitiveType)
	{
		return primitiveToWrapper.get(primitiveType);
	}

	/**
	 * Gets primitive type for specified wrapper type
	 * 
	 * @param wrapperType
	 * @return
	 */
	public static Class<?> getPrimitiveType(Class<?> wrapperType)
	{
		return wrapperToPrimitive.get(wrapperType);
	}

	/**
	 * Returns default value for specified types. For wrappers and primitives default value is returned.
	 * For non-primitive types null is returned.
	 * 
	 * @param type Type for which default value needs to be returned
	 * @return default value
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getDefaultValue(Class<T> type)
	{
		//if type is not specified 
		if(type == null)
		{
			return null;
		}

		//if primitive or its wrapper is specified
		if(type.equals(Boolean.TYPE) || type.equals(Boolean.class))
		{
			return (T)new Boolean(false);
		}
		else if(type.equals(Character.TYPE) || type.equals(Character.class))
		{
			return (T)new Character('\0');
		}
		else if(type.equals(Byte.TYPE) || type.equals(Byte.class))
		{
			return (T)new Byte((byte) 0);
		}
		else if(type.equals(Short.TYPE) || type.equals(Short.class))
		{
			return (T)new Short((short) 0);
		}
		else if(type.equals(Integer.TYPE) || type.equals(Integer.class))
		{
			return (T)new Integer(0);
		}
		else if(type.equals(Long.TYPE) || type.equals(Long.class))
		{
			return (T)new Long(0);
		}
		else if(type.equals(Float.TYPE) || type.equals(Float.class))
		{
			return (T)new Float(0);
		}
		else if(type.equals(Double.TYPE) || type.equals(Double.class))
		{
			return (T)new Double(0);
		}
		
		//if non primitive or wrapper is supported return null
		return null;
	}

	/**
	 * Checks if object of type "from" can be assigned to type "to". This method
	 * considers auto-boxing also
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public static boolean isAssignable(Class<?> from, Class<?> to)
	{
		// if its directly assignable
		if(to.isAssignableFrom(from))
		{
			return true;
		}

		// if from is wrapper
		if(wrapperToPrimitive.containsKey(from))
		{
			return wrapperToPrimitive.get(from).isAssignableFrom(to);
		}

		// if from is primitive
		if(primitiveToWrapper.containsKey(from))
		{
			return primitiveToWrapper.get(from).isAssignableFrom(to);
		}

		return false;
	}

	/**
	 * Replaces expressions in "expressionString" with the property values of
	 * "bean". The expressions should be of format ${&lt;property-name&gt;}. Where
	 * property name can be a simple property, nested property or indexed
	 * property as defined in apache's BeanUtils.getProperty
	 * 
	 * @param bean
	 * @param expressionString
	 * @param formatter
	 *            Optional. Formatter to format property values.
	 * @return
	 */
	public static String replaceExpressions(Object bean, String expressionString, IFormatter formatter)
	{
		Matcher matcher = EXPRESSION_PATTERN.matcher(expressionString);
		Matcher defValMatcher = null;
		StringBuffer result = new StringBuffer();
		Object value = null;
		String groupVal = null, key = null, defValue = null;

		// loop through the expressions
		while(matcher.find())
		{
			groupVal = matcher.group(1);
			
			defValMatcher = DEF_VALUE_PATTERN.matcher(groupVal);
			
			if(defValMatcher.matches())
			{
				key = defValMatcher.group(1);
				defValue = defValMatcher.group(2);
			}
			else
			{
				key = groupVal;
				defValue = "";
			}
			
			try
			{
				value = PropertyUtils.getProperty(bean, key);
			} catch(Exception ex)
			{
				// in case of error log a warning and ignore
				logger.warn("An error occurred while parsing expression: " + key, ex);
				value = null;
			}

			// if value is null, make it into empty string to avoid exceptions
			if(value == null)
			{
				value = defValue;
			}
			// if value is not null and formatter is specified
			else if(formatter != null)
			{
				value = formatter.convert(value);
			}

			// replace expression with property value
			matcher.appendReplacement(result, Matcher.quoteReplacement(value.toString()));
		}

		matcher.appendTail(result);

		return result.toString();
	}

	/**
	 * Finds index of "element" in specified "array". If not found returns -1.
	 * 
	 * @param array
	 * @param element
	 * @return
	 */
	public static <T> int indexOfElement(T array[], T element)
	{
		if(array == null || array.length == 0)
		{
			return -1;
		}

		for(int i = 0; i < array.length; i++)
		{
			if(element == null)
			{
				if(array[i] == null)
				{
					return i;
				}
			}
			else if(element.equals(array[i]))
			{
				return i;
			}
		}

		return -1;
	}

	/**
	 * Converts specified array of elements into Set and returns the same.
	 * Duplicate values will get filtered by set.
	 * 
	 * @param elements
	 * @return
	 */
	@SafeVarargs
	public static <E> Set<E> toSet(E... elements)
	{
		if(elements == null)
		{
			return new HashSet<E>();
		}

		return new HashSet<E>(Arrays.asList(elements));
	}

	/**
	 * Checks if the specified array is empty (null or zero length array)
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isEmptyArray(Object... array)
	{
		if(array == null)
		{
			return true;
		}

		if(array.length == 0)
		{
			return true;
		}

		return false;
	}

	/**
	 * Creates a map out of the key value pairs provided
	 * 
	 * @param keyValues
	 *            Key Value pairs
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <K, V> Map<K, V> toMap(Object... keyValues)
	{
		// if array is empty, return empty map
		if(isEmptyArray(keyValues))
		{
			return Collections.emptyMap();
		}

		// if key values are not provided in pairs
		if(keyValues.length % 2 != 0)
		{
			throw new IllegalArgumentException("Key values are not provided in pairs");
		}

		Map<K, V> keyToVal = new HashMap<K, V>();

		for(int i = 0; i < keyValues.length; i += 2)
		{
			((Map) keyToVal).put(keyValues[i], keyValues[i + 1]);
		}

		return keyToVal;
	}

	/**
	 * Fetches the specified field value of the bean. This field should be made
	 * accessible before calling this method.
	 * 
	 * @param field
	 * @param bean
	 * @return
	 */
	public static Object getFieldValue(Field field, Object bean)
	{
		try
		{
			return field.get(bean);
		} catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while fetching field value - " + field.getName(), ex);
		}
	}

	/**
	 * Converts list to map. By using "keyProp" to fetch key value and value property to fetch the value for map.
	 * Value property is optional, if not specified, current object will be used as value.
	 * 
	 * @param list List to be converted
	 * @param keyProp Key property which be used to fetch key
	 * @param valueProp Optional. Property to fetch for resultant map. If not specified, current object will be used.
	 * @return Resultant map
	 */
	@SuppressWarnings("unchecked")
	public static <K,V> Map<K, V> buildMap(Iterable<?> list, String keyProp, String valueProp)
	{
		if(list == null)
		{
			return null;
		}
		
		if(keyProp == null)
		{
			throw new NullPointerException("Key property can not be null");
		}
	
		//convert to map
		try
		{
			Map<K, V> map = new HashMap<>();
			Object value = null;
			
			for(Object obj : list)
			{
				value = (valueProp != null) ? PropertyUtils.getProperty(obj, valueProp) : obj;
				map.put((K)PropertyUtils.getProperty(obj, keyProp), (V)value);
			}
			
			return map;
		}catch(Exception ex)
		{
			throw new IllegalArgumentException("An error occurred while converting list to map", ex);
		}
	}
	
	/**
	 * Behaves like Class.forName(). But this function also support java primitives like
	 * int, float, etc (int.class and float.class respectively).
	 * @param clsType Name of the class or string version of primitive type.
	 * @return Class represented by clsType
	 */
	public static Class<?> getClass(String clsType)
	{
		if(clsType == null || clsType.trim().length() == 0)
		{
			throw new NullPointerException("Class name cannot be null or empty string.");
		}

		clsType = clsType.trim();

		if(clsType.endsWith("[]"))
		{
			int dimCount = 0;
			String clsCompType = clsType;

			while(clsCompType.endsWith("[]"))
			{
				if(clsCompType.length() == 2)
				{
					throw new IllegalArgumentException("Invalid class name specified: " + clsType);
				}

				clsCompType = clsCompType.substring(0, clsCompType.length() - 2).trim();
				dimCount++;
			}

			Class<?> compTypeCls = getClass(clsCompType);
			int dimArr[] = new int[dimCount];
			Object inst = Array.newInstance(compTypeCls, dimArr);
			return inst.getClass();
		}

		if(clsType.indexOf(".") < 0)
		{
			Class<?> primType = primitiveNameToClass.get(clsType);
			
			if(primType != null)
			{
				return primType;
			}
			
			if("void".equals(clsType))
			{
				return void.class;
			}

			try
			{
				return Class.forName("java.lang." + clsType);
			}catch(Exception ex)
			{}
		}

		try
		{
			return Class.forName(clsType);
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "Error in loading class with name: " + clsType);
		}
	}
	
}
