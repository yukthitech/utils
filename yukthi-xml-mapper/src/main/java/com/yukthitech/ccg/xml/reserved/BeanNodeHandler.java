package com.yukthitech.ccg.xml.reserved;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Locator;

import com.yukthitech.ccg.xml.BeanNode;
import com.yukthitech.ccg.xml.DefaultParserHandler;
import com.yukthitech.ccg.xml.FutureValue;
import com.yukthitech.ccg.xml.IParserHandler;
import com.yukthitech.ccg.xml.XMLAttributeMap;
import com.yukthitech.ccg.xml.util.TypeConversionUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Reserve node handler to add elements to parent collection.
 * @author akiran
 */
@NodeName(namePattern = "bean")
public class BeanNodeHandler implements IReserveNodeHandler
{
	private static final String ATTR_TYPE = "type";
	private static final String ATTR_ID = "id";
	
	@Override
	public Object createCustomNodeBean(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att, Locator locator)
	{
		String beanType = att.removeAttribute(ATTR_TYPE);
		String beanId = att.removeAttribute(ATTR_ID);
		
		if(StringUtils.isEmpty(beanType))
		{
			beanType = FutureValue.class.getName();
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
		Object actualBean = node.getActualBean();
		
		if(!(actualBean instanceof FutureValue))
		{
			return;
		}
		
		FutureValue futureValue = (FutureValue) actualBean;
		Object value = futureValue.getValue();
		
		if(!(value instanceof String))
		{
			return;
		}
		
		String strVal = (String) value;
		futureValue.setValue(TypeConversionUtils.strToObject(strVal.trim()));
	}
}
