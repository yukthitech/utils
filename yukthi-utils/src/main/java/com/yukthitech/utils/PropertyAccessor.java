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
package com.yukthitech.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Used to access bean property in more efficient way.
 * 
 * @author akranthikiran
 */
public class PropertyAccessor
{
	private static Logger logger = Logger.getLogger(PropertyAccessor.class.getName());
	
	/**
	 * Pattern for recognizing conditions within []
	 */
	private static Pattern CONDITION_PATTERN = Pattern.compile("^\\s*(.*)\\s*\\=\\s*(.*)\\s*$");
	
	private static Pattern INT_VAL_PATTERN = Pattern.compile("^\\s*(\\d+)\\s*$");
	
	private static Pattern STRING_VAL_PATTERN = Pattern.compile("^\\s*\\'(.*)\\'\\s*$");
	
	/**
	 * Enumeration of path element types.
	 * @author akranthikiran
	 */
	private static enum PathElementType
	{
		PROPERTY, INDEX, KEY, CONDITION;
	}
	
	/**
	 * Represents a single element in property path.
	 * @author akranthikiran
	 */
	private static class PathElement
	{
		/**
		 * Path till parent element excluding current property.
		 */
		private String path;
		
		/**
		 * Path including current property.
		 */
		private String fullPath;
		
		/**
		 * Type of this element.
		 */
		private PathElementType type;
		
		/**
		 * Property name or index to be accessed on current value.
		 */
		private Object key;
		
		/**
		 * In case of conditions this will be populated with sub-prop path to be used on current object. 
		 */
		private List<PathElement> conditionPath;
		
		/**
		 * Value to be matched in condition.
		 */
		private Object value;
		
		/**
		 * Flag indicating if this is an add expression. Which is helpful in inserting/adding elements into list.
		 */
		private boolean addExpression = false;

		public PathElement(String path, String fullPath, PathElementType type, Object key)
		{
			this.path = path;
			this.fullPath = fullPath;
			this.type = type;
			this.key = key;
		}
		
		public static PathElement newCondition(String path, String fullPath, List<PathElement> conditionPath, Object value)
		{
			PathElement pathElem = new PathElement(path, fullPath, PathElementType.CONDITION, null);
			pathElem.conditionPath = conditionPath;
			pathElem.value = value;
			
			return pathElem;
		}
		
		@Override
		public String toString()
		{
			StringBuilder builder = new StringBuilder("{");
			builder.append("Type: ").append(type);
			builder.append(", ").append("Path: ").append(path);
			
			if(conditionPath != null)
			{
				builder.append(", ").append("Cond Path: ").append(conditionPath);
				builder.append(", ").append("Value: ").append(value);
			}
			else
			{
				builder.append(", ").append("key: ").append(key);
			}
			
			builder.append("}");
			
			return builder.toString();
		}
	}
	
	/**
	 * Represents a property of a bean.
	 */
	public static class Property
	{
		/**
		 * Name of the property.
		 */
		private String name;
		
		/**
		 * Getter of property.
		 */
		private Method getter;
		
		/**
		 * Setter of property.
		 */
		private Method setter;
		
		/**
		 * Adder of property.
		 */
		private Method adder;
		
		/**
		 * Corresponding field of property.
		 */
		private Field field;

		public Property(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}

		public Method getGetter()
		{
			return getter;
		}

		public Method getSetter()
		{
			return setter;
		}
		
		public Method getAdder()
		{
			return adder;
		}

		public Field getField()
		{
			return field;
		}
		
		public <A extends Annotation> A getAnnotation(Class<A> type)
		{
			List<AnnotatedElement> lst = Arrays.asList(field, getter, setter, adder);
			
			for(AnnotatedElement elem : lst)
			{
				if(elem == null)
				{
					continue;
				}
				
				A res = elem.getAnnotation(type);
				
				if(res != null)
				{
					return res;
				}
			}

			return null;
		}
		
		public Object getValue(Object bean)
		{
			if(getter == null)
			{
				throw new InvalidStateException("No property get-method found with name '{}' in bean type: {}", name, bean.getClass().getName());
			}

			try
			{
				return getter.invoke(bean);
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while fetching value of property '{}' from bean of type: {}", name, bean.getClass().getName(), ex);
			}
		}
		
		public void setValue(Object bean, Object value)
		{
			if(setter == null)
			{
				throw new InvalidStateException("No property set-method found with name '{}' in bean type: {}", name, bean.getClass().getName());
			}

			try
			{
				setter.invoke(bean, value);
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while setting value of property '{}' on bean of type: {}", name, bean.getClass().getName(), ex);
			}
		}
		
		public Class<?> getType()
		{
			if(field != null)
			{
				return field.getType();
			}
			
			if(getter != null)
			{
				return getter.getReturnType();
			}
			
			if(setter != null)
			{
				return setter.getParameterTypes()[0];
			}
			
			if(adder != null)
			{
				return adder.getParameterTypes()[0];
			}
			
			return null;
		}
		
		public Type getGenericType()
		{
			if(field != null)
			{
				return field.getGenericType();
			}
			
			if(getter != null)
			{
				return getter.getGenericReturnType();
			}
			
			if(setter != null)
			{
				return setter.getGenericParameterTypes()[0];
			}
			
			if(adder != null)
			{
				return adder.getGenericParameterTypes()[0];
			}
			
			return null;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			StringBuilder builder = new StringBuilder(super.toString());
			builder.append("[");

			builder.append("Name: ").append(name);
			builder.append(",").append("field: ").append(field != null ? field.getName() : "null");
			builder.append(",").append("getter: ").append(getter != null ? getter.getName() : "null");
			builder.append(",").append("setter: ").append(setter != null ? setter.getName() : "null");
			builder.append(",").append("adder: ").append(adder != null ? adder.getName() : "null");

			builder.append("]");
			return builder.toString();
		}

	}
	
	private static Map<Class<?>, Map<String, Property>> propertyCache = new HashMap<>();
	
	public static synchronized Map<String, Property> getProperties(Class<?> beanType)
	{
		Map<String, Property> propMap = propertyCache.get(beanType);
		
		if(propMap != null)
		{
			return propMap;
		}
		
		Map<String, Field> fieldMap = new HashMap<>();
		Map<String, Method> setterMap = new HashMap<>();
		Map<String, Method> getterMap = new HashMap<>();
		Map<String, Method> adderMap = new HashMap<>();
		
		Set<String> names = new HashSet<String>();
		
		boolean firstTime = true;
		
		while(!beanType.getName().startsWith("java"))
		{
			Field[] fields = beanType.getDeclaredFields();
			
			for(Field field : fields)
			{
				fieldMap.put(field.getName(), field);
				names.add(field.getName());
			}
			
			if(!firstTime)
			{
				beanType = beanType.getSuperclass();
				continue;
			}
			
			// As only public methods are accessed, the method list is obtained on direct type
			//   for parent classes it is skipped (as parent classes would have been already processed).
			firstTime = false;
			Method[] methods = beanType.getMethods();
			
			for(Method method : methods)
			{
				String name = method.getName();
				Map<String, Method> map = null;
				
				if(name.startsWith("set") && name.length() > 3 && method.getParameterCount() == 1)
				{
					map = setterMap;
					name = name.substring(3);
				}
				else if(name.startsWith("add") && name.length() > 3 && method.getParameterCount() == 1)
				{
					map = adderMap;
					name = name.substring(3);
				}
				else if(name.startsWith("get") && name.length() > 3 && method.getParameterCount() == 0 && !void.class.equals(method.getReturnType()))
				{
					map = getterMap;
					name = name.substring(3);
				}
				else if(name.startsWith("is") && name.length() > 2 && method.getParameterCount() == 0 && boolean.class.equals(method.getReturnType()))
				{
					map = getterMap;
					name = name.substring(2);
				}
				else
				{
					continue;
				}
				
				// ignore methods which are not starting with capital letter
				//    after removing prefix
				if(!Character.isUpperCase(name.charAt(0)))
				{
					continue;
				}
				
				name = Character.toLowerCase(name.charAt(0)) + (name.length() == 1 ? "" : name.substring(1));
				map.put(name, method);
				names.add(name);
			}
			
			beanType = beanType.getSuperclass();
		}
		
		propMap = new HashMap<>();

		for(String name : names)
		{
			Property prop = new Property(name);
			prop.adder = adderMap.get(name);
			prop.field = fieldMap.get(name);
			prop.getter = getterMap.get(name);
			prop.setter = setterMap.get(name);
			
			propMap.put(name, prop);
		}
		
		propMap = Collections.unmodifiableMap(propMap);
		
		propertyCache.put(beanType, propMap);
		return propMap;
	}
	
	public static Property getProperty(Class<?> beanType, String name)
	{
		Map<String, Property> propMap = getProperties(beanType);
		return propMap.get(name);
	}
	
	/**
	 * Create a new element based on info specified into elements.
	 * @param path path at which element is being added
	 * @param fullPath path along with current element
	 * @param builder builder which represents current key
	 * @param expectedType element type expected to be created
	 * @param elements list to which created element to be added
	 * @return true if element addition is successful
	 */
	private static boolean addElement(String path, String fullPath, StringBuilder builder, PathElementType expectedType, List<PathElement> elements)
	{
		if(builder.length() == 0)
		{
			return false;
		}
		
		if(expectedType == PathElementType.PROPERTY || expectedType == PathElementType.KEY)
		{
			elements.add(new PathElement(path, fullPath, expectedType, builder.toString()));
			builder.setLength(0);
			return true;
		}
		
		//when expected type is index (specified in square brackets)
		try
		{
			String expr = builder.toString();
			
			//before parsing as index, check if current one is condition
			Matcher condMatcher = CONDITION_PATTERN.matcher(expr);
			
			if(condMatcher.matches())
			{
				List<PathElement> condPath = parse(condMatcher.group(1));
				Object value = parseValue(condMatcher.group(2));
				
				elements.add(PathElement.newCondition(path, fullPath, condPath, value));
				
				builder.setLength(0);
				return true;
			}
			
			//parse the value as index
			Integer idx = "+".equals(expr) ? null : Integer.parseInt(expr);
			PathElement newElem = new PathElement(path, fullPath, PathElementType.INDEX, idx);
			newElem.addExpression = expr.startsWith("+");
			
			elements.add(newElem);
			
			builder.setLength(0);
			return true;
		} catch(Exception ex)
		{
			throw new InvalidArgumentException("Invalid index '{}' specified for: {}", builder.toString(), path, ex);
		}
	}
	
	private static Object parseValue(String valStr)
	{
		Matcher matcher = INT_VAL_PATTERN.matcher(valStr);
		
		if(matcher.matches())
		{
			return Integer.parseInt(matcher.group(1));
		}
		
		matcher = STRING_VAL_PATTERN.matcher(valStr);
		
		if(matcher.matches())
		{
			return matcher.group(1);
		}
		
		throw new InvalidArgumentException("Non value token encountered when value was expected: {}", valStr);
	}
	
	/**
	 * Collects the content between brackets recursively.
	 *
	 * @param chArr
	 *            the ch arr
	 * @param idx
	 *            the idx
	 * @param builder
	 *            the builder
	 * @param curPath
	 *            the cur path
	 * @param squareBracket
	 *            the square bracket
	 * @return the int
	 */
	private static int collectBracketContent(char chArr[], int idx, StringBuilder builder, String curPath, boolean squareBracket)
	{
		int len = chArr.length;
		int finalIdx = -1;
		int initialLen = builder.length();
		
		for(int i = idx; i < len; i++)
		{
			char ch = chArr[i];
			
			if(ch == ')')
			{
				if(!squareBracket)
				{
					finalIdx = i;
					break;
				}
				
				throw new InvalidArgumentException("For property {} at index {} encountered ')' when expecting ']'", curPath, i);
			}
			
			if(ch == ']')
			{
				if(squareBracket)
				{
					finalIdx = i;
					break;
				}
				
				throw new InvalidArgumentException("For property {} at index {} encountered ']' when expecting ')'", curPath, i);
			}
			
			//if sub bracket is found, extract the content recursively
			if(ch == '(')
			{
				builder.append('(');
				i = collectBracketContent(chArr, i + 1, builder, curPath, false);
				builder.append(')');
				continue;
			}

			//if sub bracket is found, extract the content recursively
			if(ch == '[')
			{
				builder.append('[');
				i = collectBracketContent(chArr, i + 1, builder, curPath, true);
				builder.append(']');
				continue;
			}
			
			builder.append(ch);
		}
		
		if(finalIdx == -1)
		{
			throw new InvalidArgumentException("For property {} no closing bracket '{}' found.", curPath, (squareBracket ? '[' : ')'));
		}
		
		if(initialLen == builder.length())
		{
			throw new InvalidArgumentException("For property {} no content found in brackets.", curPath);
		}
		
		return finalIdx;
	}
	
	/**
	 * Parses the given property path into path elements.
	 * @param path
	 * @return
	 */
	private static List<PathElement> parse(String path)
	{
		if(StringUtils.isBlank(path))
		{
			throw new InvalidArgumentException("Null or empty property path specified.");
		}
		
		path = path.trim();
		char chArr[] = path.toCharArray();
		StringBuilder builder = new StringBuilder();
		String curPath = "";
		
		List<PathElement> elements = new ArrayList<>();
		int len = chArr.length;
		String newPath  = null;
		
		for(int i = 0; i < len; i++)
		{
			char ch = chArr[i];
			
			/*
			 * TODO: Disabling () expressions to access map keys. When this support is to be added,
			 * 	it should support - string values enclosed in '', integer values and sub expressions.
			 */
			//if(ch == '(' || ch == '[')
			if(ch == '[')
			{
				newPath = new String(chArr, 0, i);
				addElement(curPath, newPath, builder, PathElementType.PROPERTY, elements);

				curPath = newPath;
				
				i = collectBracketContent(chArr, i + 1, builder, curPath, (ch == '['));
				newPath = new String(chArr, 0, i + 1);
				
				addElement(curPath, newPath, builder, 
						(ch == '[') ? PathElementType.INDEX : PathElementType.KEY, 
						elements);
				
				curPath = newPath;
				continue;
			}
			
			if(ch == '.')
			{
				newPath = new String(chArr, 0, i);
				addElement(curPath, newPath, builder, PathElementType.PROPERTY, elements);
				
				curPath = newPath;
				continue;
			}
			
			if(Character.isAlphabetic(ch) || Character.isDigit(ch) || Character.isWhitespace(ch) || ch == '-' || ch == '@')
			{
				builder.append(ch);
				continue;
			}
			
			throw new InvalidStateException("Unsupported character encountered: {}", ch);
		}
		
		//if any content is left over at end
		if(builder.length() > 0)
		{
			addElement(curPath, path, builder, PathElementType.PROPERTY, elements);
		}
		
		return elements;
	}
	
	/**
	 * Fetches the specified property value from specified bean.
	 * @param bean
	 * @param prop
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static Object getBeanProperty(Object bean, String prop)
	{
		//handle special property - @this
		if("@this".equals(prop))
		{
			return bean;
		}
		
		if(bean instanceof Map)
		{
			return ((Map) bean).get(prop);
		}
		
		Property property = getProperties(bean.getClass()).get(prop);
		
		if(property == null)
		{
			throw new InvalidStateException("No property found with name '{}' in bean type: {}", prop, bean.getClass().getName());
		}
		
		return property.getValue(bean);
	}
	
	/**
	 * Sets the specified property value on specified bean.
	 * @param bean
	 * @param prop
	 * @param value
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void setBeanProperty(Object bean, String prop, Object value)
	{
		if(bean instanceof Map)
		{
			((Map) bean).put(prop, value);
			return;
		}
		
		Property property = getProperties(bean.getClass()).get(prop);
		
		if(property == null)
		{
			throw new InvalidStateException("No property found with name '{}' in bean type: {}", prop, bean.getClass().getName());
		}
		
		if(property.setter == null)
		{
			throw new InvalidStateException("No property set-method found with name '{}' in bean type: {}", prop, bean.getClass().getName());
		}
		
		property.setValue(bean, value);
	}

	/**
	 * Fetches the value from the bean using specified property path till specified index.
	 * @param bean bean from which value needs to be extracted
	 * @param path property path to be extracted
	 * @param tillIdx index till which evaluation needs to be done
	 * @param throwErrOnNull if true, throws exception if any null is found on the path
	 * @return matching value
	 */
	@SuppressWarnings("unchecked")
	private static Object getValue(Object bean, List<PathElement> path, int tillIdx, boolean throwErrOnNull)
	{
		Object curValue = bean;
		
		for(int i = 0; i < tillIdx; i++)
		{
			PathElement elem = path.get(i);
			
			switch(elem.type)
			{
				case PROPERTY:
				{
					curValue = getBeanProperty(curValue, (String) elem.key);
					break;
				}
				case KEY:
				{
					curValue = getBeanProperty(curValue, (String) elem.key);
					break;
				}
				case INDEX:
				{
					if(curValue instanceof List)
					{
						curValue = ((List<Object>) curValue).get((Integer) elem.key);
					}
					else if(curValue.getClass().isArray())
					{
						curValue = Array.get(curValue, (Integer) elem.key);
					}
					else
					{
						throw new InvalidArgumentException("Index is used on non-list value at: {}", elem.path);
					}
					
					break;
				}
				case CONDITION:
				{
					if(!(curValue instanceof Collection))
					{
						throw new InvalidArgumentException("Condition is used on non-collection value at: {}", elem.path);
					}
					
					Collection<Object> collection = (Collection<Object>) curValue;
					Object matchedValue = null;
					
					for(Object obj : collection)
					{
						Object condValue = getValue(obj, elem.conditionPath, elem.conditionPath.size(), false);
						
						if(Objects.equals("" + condValue, "" + elem.value))
						{
							matchedValue = obj;
							break;
						}
					}

					curValue = matchedValue;
					break;
				}
			}
			
			if(curValue == null)
			{
				if(throwErrOnNull)
				{
					throw new NullPointerException(String.format("Property path '%s' resulted in null", elem.fullPath));
				}
				
				break;
			}
		}
	
		return curValue;
	}
	
	/**
	 * Fetches specified composite property from the specified bean.
	 * @param bean bean from which property to be fetched
	 * @param property property path to fetch
	 * @return property value
	 */
	public static Object getProperty(Object bean, String property)
	{
		List<PathElement> pathElemLst = parse(property);
		return getValue(bean, pathElemLst, pathElemLst.size(), false);
	}

	/**
	 * Fetches specified composite property from the specified bean.
	 * @param bean bean from which property to be fetched
	 * @param property property path to fetch
	 * @param throwErrorOnNull if true, any null occurs on the path, NullPointerException is thrown indicating the path
	 * @return property value
	 */
	public static Object getProperty(Object bean, String property, boolean throwErrorOnNull)
	{
		List<PathElement> pathElemLst = parse(property);
		return getValue(bean, pathElemLst, pathElemLst.size(), throwErrorOnNull);
	}

	/**
	 * Sets the specified composite property on specified bean.
	 * @param bean bean on which property to be set
	 * @param property property path to set
	 * @param value value to set
	 */
	@SuppressWarnings("unchecked")
	public static void setProperty(Object bean, String property, Object value)
	{
		List<PathElement> pathElemLst = parse(property);
		
		Object parent = getValue(bean, pathElemLst, pathElemLst.size() - 1, true);
		
		PathElement lastElem = pathElemLst.get(pathElemLst.size() - 1);
		
		switch(lastElem.type)
		{
			case PROPERTY:
			{
				setBeanProperty(parent, (String) lastElem.key, value);
				break;
			}
			case KEY:
			{
				setBeanProperty(parent, (String) lastElem.key, value);
				break;
			}
			case INDEX:
			{
				if(!(parent instanceof Collection))
				{
					throw new InvalidArgumentException("Index is used on non-collection value at: {}", lastElem.path);
				}

				Collection<Object> parentCollection = (Collection<Object>) parent;
				
				if(!(parentCollection instanceof List))
				{
					if(lastElem.addExpression)
					{
						parentCollection.add(value);
						return;
					}
					
					throw new InvalidArgumentException("Index is used on non-list value at: {}", lastElem.path);
				}
				
				List<Object> parentList = (List<Object>) parent;
				Integer idx = (Integer) lastElem.key;
				
				if(lastElem.addExpression || idx == parentList.size())
				{
					if(idx == null)
					{
						parentList.add(value);
					}
					else
					{
						parentList.add(idx, value);
					}
				}
				//if set has to be done
				else
				{
					parentList.set(idx, value);
				}
				
				break;
			}
			case CONDITION:
			{
				throw new InvalidArgumentException("Condition is used as last property to set property: {}", lastElem.fullPath);
			}
		}
	}

	/**
	 * Removes the specified property from specified bean.
	 * @param bean bean from which property needs to be removed.
	 * @param property property to be removed.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void removeProperty(Object bean, String property)
	{
		List<PathElement> pathElemLst = parse(property);
		
		Object parent = getValue(bean, pathElemLst, pathElemLst.size() - 1, true);
		
		PathElement lastElem = pathElemLst.get(pathElemLst.size() - 1);
		
		switch(lastElem.type)
		{
			case PROPERTY:
			{
				if(parent instanceof Map)
				{
					((Map<Object, Object>) parent).remove(lastElem.key);
				}
				else
				{
					setBeanProperty(parent, (String) lastElem.key, null);
				}
				
				break;
			}
			case KEY:
			{
				if(parent instanceof Map)
				{
					((Map<Object, Object>) parent).remove(lastElem.key);
				}
				else
				{
					setProperty(parent, (String) lastElem.key, null);
				}
				
				break;
			}
			case INDEX:
			{
				if(!(parent instanceof List))
				{
					throw new InvalidArgumentException("Index is used on non-list value at: {}", lastElem.path);
				}
				
				int idx = (Integer) lastElem.key;
				((List<Object>) parent).remove(idx);
				
				break;
			}
			case CONDITION:
			{
				Collection<Object> collection = null;
				
				if(parent instanceof Map)
				{
					collection = ((Map) parent).entrySet();
				}
				else if(parent instanceof Collection)
				{
					collection = (Collection<Object>) parent;
				}
				else
				{
					throw new InvalidArgumentException("Condition is used on non-collection value at: {}", lastElem.path);
				}
				
				Iterator<Object> it = collection.iterator();
				
				while(it.hasNext())
				{
					Object obj = it.next();
					Object condValue = getValue(obj, lastElem.conditionPath, lastElem.conditionPath.size(), false);
					
					if(Objects.equals("" + condValue, "" + lastElem.value))
					{
						it.remove();
					}
				}
			}
		}
	}
	
	public static Map<String, Object> describe(Object bean)
	{
		Map<String, Property> propMap = getProperties(bean.getClass());
		Map<String, Object> res = new HashMap<>();
		
		for(Property prop : propMap.values())
		{
			if(prop.getter != null)
			{
				res.put(prop.name, getBeanProperty(bean, prop.name));
			}
		}
		
		return res;
	}
	
	public static <T> T cloneObject(Object source, Class<T> targetType)
	{
		if(source == null)
		{
			return null;
		}
		
		T target = null;
		
		try
		{
			target = targetType.getConstructor().newInstance();
		}catch(Exception ex)
		{
			throw new InvalidStateException("Failed to created instance of target type: {}", targetType.getName(), ex);
		}
		
		Map<String, Property> destProperties = getProperties(targetType);
		Map<String, Property> srcProperties = getProperties(source.getClass());
		
		for(Map.Entry<String, Property> srcProp : srcProperties.entrySet())
		{
			Property targetProp = destProperties.get(srcProp.getKey());
			
			if(targetProp == null || targetProp.setter == null)
			{
				continue;
			}
			
			Object value = srcProp.getValue().getValue(source);
			
			if(value == null)
			{
				continue;
			}
			
			value = ConvertUtils.convert(value, targetProp.getType());
			
			try
			{
				targetProp.setValue(target, value);
			}catch(Exception ex)
			{
				logger.warning(String.format("Failed to set property '{}' on target. Error: {}", srcProp.getKey(), "" + ex));
			}
		}
		
		return target;
	}
}
