package com.yukthitech.ccg.xml.reserved;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Locator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.ccg.xml.BeanNode;
import com.yukthitech.ccg.xml.IParserHandler;
import com.yukthitech.ccg.xml.XMLAttributeMap;
import com.yukthitech.ccg.xml.XMLLoadException;

/**
 * Reserve node handler to parse json as object.
 * @author akiran
 */
@NodeName(namePattern = "json")
public class JsonNodeHandler implements IReserveNodeHandler
{
	/**
	 * Used to parse json.
	 */
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	@Override
	public Object createCustomNodeBean(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att, Locator locator)
	{
		String name = att.get("property", null);
		
		if(StringUtils.isBlank(name))
		{
			throw new XMLLoadException("Mandatory 'property' attribute not specified.", node, locator);
		}
		
		return null;
	}

	@Override
	public void handleCustomNodeEnd(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att, Locator locator)
	{
		if(!node.isTextNode())
		{
			throw new XMLLoadException("Non text node is defined as json node", node, locator);
		}
		
		Class<?> beanType = Object.class;
		String clsName = att.get("beanType", null);
		
		if(StringUtils.isNotBlank(clsName))
		{
			try
			{
				beanType = Class.forName(clsName);
			}catch(Exception ex)
			{
				throw new XMLLoadException("Invalid bean type specified for json parsing: " + clsName, ex, node, locator);
			}
		}
		
		Object value = null;
		
		try
		{
			value = OBJECT_MAPPER.readValue(node.getText(), beanType);
		}catch(Exception ex)
		{
			throw new XMLLoadException("Failed to load json to type: " + beanType.getName(), ex, node, locator);
		}
		
		Object parent = node.getParent();
		String name = att.get("name", null);
		
		try
		{
			PropertyUtils.setProperty(parent, name, value);
		}catch(Exception ex)
		{
			throw new XMLLoadException("Failed to set bean of type '" + beanType.getName() + "' as property '" + name + "' on bean of type: " + parent.getClass().getName(), ex, node, locator);
		}
	}
}
