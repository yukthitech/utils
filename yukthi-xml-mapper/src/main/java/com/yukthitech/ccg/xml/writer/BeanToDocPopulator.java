package com.yukthitech.ccg.xml.writer;

import static com.yukthitech.ccg.xml.writer.XmlBeanWriter.CCG_PREFIX;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.yukthitech.ccg.xml.DefaultParserHandler;
import com.yukthitech.ccg.xml.annotations.XmlAttribute;
import com.yukthitech.ccg.xml.annotations.XmlElement;
import com.yukthitech.ccg.xml.annotations.XmlIgnore;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.beans.BeanProperty;

/**
 * Populates data from bean to document.
 * @author akiran
 */
class BeanToDocPopulator
{
	/**
	 * Returns true if specified type is supported in attribute.
	 * @param type
	 * @return
	 */
	private static boolean isAttributeType(Class<?> type)
	{
		if(type.isPrimitive() || CommonUtils.isWrapperClass(type))
		{
			return true;
		}
		
		if(String.class.equals(type) || Date.class.equals(type) || Class.class.equals(type))
		{
			return true;
		}

		return type.isEnum();
	}
	
	@SuppressWarnings("rawtypes")
	private static String toAttributeString(Object value)
	{
		if(value instanceof Date)
		{
			return XmlBeanWriter.DEF_DATE_FORMAT.format(value);
		}
		
		if(value instanceof Class)
		{
			return ((Class) value).getName();
		}
		
		return value.toString();
	}
	
	/**
	 * Adds attribute property to specified element.
	 * @param document
	 * @param element
	 * @param value
	 * @param prop
	 */
	private static void addAttributableProperty(Document document, Element element, Object value, BeanProperty prop)
	{
		XmlAttribute attr = prop.getAnnotation(XmlAttribute.class);
		String valueStr = toAttributeString(value);
		
		if(attr != null)
		{
			String name = StringUtils.isBlank(attr.name()) ? prop.getName() : attr.name();
			element.setAttribute(name, valueStr);
			return;
		}
		
		XmlElement elem = prop.getAnnotation(XmlElement.class);
		String name = ( elem == null || StringUtils.isBlank(elem.name()) ) ? prop.getName() : elem.name();
		
		Element propElem = document.createElement(name);
		element.appendChild(propElem);
		
		if(elem != null && elem.cdata())
		{
			propElem.appendChild( document.createCDATASection(valueStr) );
		}
		else
		{
			propElem.appendChild( document.createTextNode(valueStr) );
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void populateCollectionNode(Document document, Element lstElem, Object value, Type genericType, XmlWriterConfig writerConfig)
	{
		//fetch element type
		Type elementType = null;
		
		if((genericType instanceof ParameterizedType))
		{
			elementType = ((ParameterizedType)genericType).getActualTypeArguments()[0];			
		}
		
		Collection<Object> lst = (Collection) value;
		Element elemElem = null; 
				
		for(Object elemBean : lst)
		{
			elemElem = document.createElement(CCG_PREFIX + "element");
			lstElem.appendChild(elemElem);
			
			if(elementType != null && isAttributeType((Class<?>)elementType))
			{
				String valueStr = toAttributeString(elemBean);
				elemElem.appendChild(document.createTextNode(valueStr));
			}
			else
			{
				populateElement(document, elemElem, elemBean, elementType, writerConfig);
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void populateMapNode(Document document, Element mapElem, Object value, Type genericType, XmlWriterConfig writerConfig)
	{
		//fetch element type
		Type valueType = Object.class;

		if(genericType != null && (genericType instanceof ParameterizedType))
		{
			valueType = ((ParameterizedType)genericType).getActualTypeArguments()[1];
		}
		
		Map<Object, Object> map = (Map) value;
		Element entryElem = null; 
				
		for(Object key : map.keySet())
		{
			Object mapValue = map.get(key);
			
			entryElem = document.createElement(CCG_PREFIX + "entry");
			mapElem.appendChild(entryElem);
			
			entryElem.setAttribute("key", toAttributeString(key));
			
			if(valueType != null && isAttributeType((Class<?>)valueType))
			{
				String valueStr = toAttributeString(mapValue);
				entryElem.appendChild(document.createTextNode(valueStr));
			}
			else
			{
				populateElement(document, entryElem, mapValue, valueType, writerConfig);
			}
		}
	}

	/**
	 * Creates sub node element.
	 * @param document
	 * @param parentElem
	 * @param value
	 * @param prop
	 */
	private static void createSubnode(Document document, Element parentElem, Object value, BeanProperty prop, XmlWriterConfig writerConfig)
	{
		XmlElement elem = prop.getAnnotation(XmlElement.class);
		String name = ( elem == null || StringUtils.isBlank(elem.name()) ) ? prop.getName() : elem.name();
		
		Element newElem = document.createElement(name);
		parentElem.appendChild(newElem);
		
		populateElement(document, newElem, value, prop.getReadMethod().getGenericReturnType(), writerConfig);
	}
	
	/**
	 * Adds attributes and sub nodes recursively to specified document element.
	 * @param document
	 * @param element
	 * @param bean
	 */
	public static void populateElement(Document document, Element element, Object bean, Type declaredType, XmlWriterConfig writerConfig)
	{
		Class<?> rawDeclaredType = null;
		
		if(declaredType instanceof Class)
		{
			rawDeclaredType = (Class<?>) declaredType;
		}
		else if(declaredType instanceof ParameterizedType)
		{
			rawDeclaredType = (Class<?>) ((ParameterizedType) declaredType).getRawType();
		}
		
		if(rawDeclaredType != null && 
				!(bean instanceof Collection) &&
				!(bean instanceof Map) &&
				!rawDeclaredType.equals(bean.getClass()))
		{
			element.setAttribute(CCG_PREFIX + DefaultParserHandler.ATTR_BEAN_TYPE, bean.getClass().getName());
		}
		
		if(bean instanceof Collection)
		{
			populateCollectionNode(document, element, bean, declaredType, writerConfig);
			return;
		}
		
		if(bean instanceof Map)
		{
			populateMapNode(document, element, bean, declaredType, writerConfig);
			return;
		}
		
		if(isAttributeType(bean.getClass()) || bean.getClass().isEnum())
		{
			element.appendChild(document.createTextNode(toAttributeString(bean)));
			return;
		}

		List<BeanProperty> properties = XmlBeanWriter.getReadProperties(bean.getClass(), writerConfig);
		Object value = null;
		
		for(BeanProperty prop : properties)
		{
			//skip property which is marked with @XmlIgnore
			if(prop.getAnnotation(XmlIgnore.class) != null)
			{
				continue;
			}
			
			value = prop.getValue(bean);
			
			if(value == null)
			{
				continue;
			}
			
			if(isAttributeType(prop.getType()))
			{
				addAttributableProperty(document, element, value, prop);
				continue;
			}
			
			createSubnode(document, element, value, prop, writerConfig);
		}
	}
}
