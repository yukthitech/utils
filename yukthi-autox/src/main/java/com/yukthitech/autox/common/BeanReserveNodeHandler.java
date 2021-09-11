package com.yukthitech.autox.common;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.InvalidArgumentException;
import org.xml.sax.Locator;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.filter.ExpressionFactory;
import com.yukthitech.ccg.xml.BeanNode;
import com.yukthitech.ccg.xml.IParserHandler;
import com.yukthitech.ccg.xml.XMLAttributeMap;
import com.yukthitech.ccg.xml.reserved.IReserveNodeHandler;
import com.yukthitech.ccg.xml.reserved.NodeName;

@NodeName(namePattern = "registerBean")
public class BeanReserveNodeHandler implements IReserveNodeHandler
{
	public static class BeanInfo
	{
		private String id;
		
		private String value;
		
		public void setId(String id)
		{
			this.id = id;
		}
		
		public void setValue(String value)
		{
			this.value = value;
		}
	}
	
	@Override
	public Object createCustomNodeBean(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att, Locator locator)
	{
		return new BeanInfo();
	}

	@Override
	public void handleCustomNodeEnd(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att, Locator locator)
	{
		BeanInfo info = (BeanInfo) node.getActualBean();
		
		if(StringUtils.isEmpty(info.id))
		{
			throw new InvalidArgumentException("No id specified for bean registration");
		}
		
		if(StringUtils.isBlank(info.value))
		{
			throw new InvalidArgumentException("No value specified for bean registration");
		}
		
		AutomationContext automationContext = AutomationContext.getInstance();
		ExpressionFactory exprFactory = ExpressionFactory.getExpressionFactory();
		
		Object bean = exprFactory.parseExpression(automationContext, info.value);
		parserHandler.registerBean(info.id, bean);
	}
}
