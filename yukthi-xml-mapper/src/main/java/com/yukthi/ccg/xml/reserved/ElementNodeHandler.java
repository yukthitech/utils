package com.yukthi.ccg.xml.reserved;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import com.yukthi.ccg.xml.BeanNode;
import com.yukthi.ccg.xml.IParserHandler;
import com.yukthi.ccg.xml.XMLAttributeMap;
import com.yukthi.ccg.xml.XMLUtil;
import com.yukthi.utils.exceptions.InvalidStateException;

/**
 * Reserve node handler to add elements to parent collection.
 * @author akiran
 */
@NodeName(namePattern = "element")
public class ElementNodeHandler implements IReserveNodeHandler
{
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object createCustomNodeBean(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att)
	{
		Object parent = node.getParent();
		
		if(!(parent instanceof Collection))
		{
			throw new InvalidStateException("Reserve node <element> encountered under non-collection node");
		}
		
		Type genericType = node.getParentNode().getGenericType();
		
		if(genericType == null || !(genericType instanceof ParameterizedType))
		{
			throw new InvalidStateException("For <element> failed to determine the parent collection element type");
		}
		
		Class<?> elementType = (Class<?>)((ParameterizedType)genericType).getActualTypeArguments()[0];
		
		node.setType(elementType);
		node.setActualType(elementType);
		
		if(XMLUtil.isSupportedAttributeClass(elementType))
		{
			node.setTextNodeFlag(true);
			return null;
		}
		
		Object result = parserHandler.createBean(node, att);
		((Collection) parent).add(result);
		
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
		Collection<Object> parent = (Collection) node.getParent();
		
		parent.add(value);
	}
}