package com.yukthitech.ccg.xml.reserved;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Locator;

import com.yukthitech.ccg.xml.BeanNode;
import com.yukthitech.ccg.xml.DefaultParserHandler;
import com.yukthitech.ccg.xml.IParserHandler;
import com.yukthitech.ccg.xml.XMLAttributeMap;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Reserve node handler to add elements to parent collection.
 * @author akiran
 */
@NodeName(namePattern = "bean")
public class BeanNodeHandler implements IReserveNodeHandler
{
	@Override
	public Object createCustomNodeBean(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att, Locator locator)
	{
		String beanType = att.get(DefaultParserHandler.ATTR_BEAN_TYPE, null);
		String beanId = att.get(DefaultParserHandler.ATTR_BEAN_ID, null);
		
		if(StringUtils.isEmpty(beanType))
		{
			throw new InvalidStateException("No 'type' is specified for 'bean' node");
		}
				
		if(StringUtils.isEmpty(beanId))
		{
			throw new InvalidStateException("No 'id' is specified for 'bean' node");
		}
		
		att.put(DefaultParserHandler.ATTR_BEAN_TYPE, beanType, true);
		att.put(DefaultParserHandler.ATTR_BEAN_ID, beanId, true);
		
		Object bean = parserHandler.createBean(node, att);
		return bean;
	}

	@Override
	public void handleCustomNodeEnd(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att, Locator locator)
	{
	}
}
