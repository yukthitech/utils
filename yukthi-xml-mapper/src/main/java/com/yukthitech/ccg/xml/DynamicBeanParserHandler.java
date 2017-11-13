package com.yukthitech.ccg.xml;

/**
 * Parser handler that can be used to convert xml into dynamic bean instead of standard beans.
 * @author akiran
 */
public class DynamicBeanParserHandler extends DefaultParserHandler
{
	@Override
	public Object createRootBean(BeanNode node, XMLAttributeMap att)
	{
		return new DynamicBean();
	}

	@Override
	public Object createBean(BeanNode node, XMLAttributeMap att, ClassLoader loader)
	{
		return new DynamicBean();
	}

	@Override
	public Object createAttributeBean(BeanNode node, String attName, Class<?> type)
	{
		return new DynamicBean();
	}
	
}
