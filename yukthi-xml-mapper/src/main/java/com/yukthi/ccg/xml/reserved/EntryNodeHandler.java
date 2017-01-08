package com.yukthi.ccg.xml.reserved;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import com.yukthi.ccg.xml.BeanNode;
import com.yukthi.ccg.xml.IParserHandler;
import com.yukthi.ccg.xml.XMLAttributeMap;
import com.yukthi.ccg.xml.XMLLoadException;
import com.yukthi.ccg.xml.XMLUtil;
import com.yukthi.utils.exceptions.InvalidStateException;

/**
 * Reserve node handler to load entries into parent map.
 * @author akiran
 */
@NodeName(namePattern = "entry")
public class EntryNodeHandler implements IReserveNodeHandler
{
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object createCustomNodeBean(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att)
	{
		Object parent = node.getParent();
		
		if(!(parent instanceof Map))
		{
			throw new XMLLoadException("Reserve node <entry> encountered under non-map node", node);
		}
		
		Type genericType = node.getParentNode().getGenericType();

		if(genericType == null || !(genericType instanceof ParameterizedType))
		{
			throw new InvalidStateException("For <entry> failed to determine the parent Map key/value type");
		}
		
		Class<?> keyType = (Class<?>)((ParameterizedType)genericType).getActualTypeArguments()[0];
		Class<?> valueType = (Class<?>)((ParameterizedType)genericType).getActualTypeArguments()[1];
		
		if(!XMLUtil.isSupportedAttributeClass(keyType))
		{
			throw new XMLLoadException("<entry> reserved node does not support non-attributable key type - " + keyType.getName(), node);
		}
		
		String keyStr = att.get("key", null);
		
		if(keyStr == null)
		{
			throw new XMLLoadException("No 'key' attribute specified for <entry> node", node);
		}
		
		//parse the key 
		Object key = XMLUtil.parseAttributeObject(keyStr, keyType, parserHandler.getDateFormat());
		att.remove("key");
		node.getAttributeMap().removeAttribute("key");
		
		node.setType(valueType);
		node.setActualType(valueType);

		if(XMLUtil.isSupportedAttributeClass(valueType))
		{
			//set key on context so that it can be used during end node processing
			node.setContextAttribute("key", key);
			
			node.setTextNodeFlag(true);
			return null;
		}

		Object result = parserHandler.createBean(node, att);
		((Map) parent).put(key, result);
		
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void handleCustomNodeEnd(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att)
	{
		if(!node.isTextNode())
		{
			return;
		}
		
		Object value = parserHandler.parseTextNodeValue(node, att);
		Map<Object, Object> parent = (Map) node.getParent();
		
		parent.put(node.getContextAttribute("key"), value);
	}
}
