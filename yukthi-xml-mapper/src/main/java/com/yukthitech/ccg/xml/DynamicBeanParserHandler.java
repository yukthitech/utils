package com.yukthitech.ccg.xml;

/**
 * Parser handler that can be used to convert xml into dynamic bean instead of standard beans.
 * @author akiran
 */
public class DynamicBeanParserHandler extends DefaultParserHandler
{
	/**
	 * If this flag enabled, based on prefix values will be type converted.
	 */
	private boolean typeConversationEnabled;
	
	/**
	 * Sets the type conversation enabled.
	 *
	 * @param typeConversationEnabled the new type conversation enabled
	 */
	public void setTypeConversationEnabled(boolean typeConversationEnabled)
	{
		this.typeConversationEnabled = typeConversationEnabled;
	}
	
	@Override
	public Object createRootBean(BeanNode node, XMLAttributeMap att)
	{
		return new DynamicBean(typeConversationEnabled);
	}

	@Override
	public Object createBean(BeanNode node, XMLAttributeMap att, ClassLoader loader)
	{
		if(DynamicBeanList.class.equals(node.getActualType()))
		{
			return new DynamicBeanList(typeConversationEnabled);
		}
		
		return new DynamicBean(typeConversationEnabled);
	}

	@Override
	public Object createAttributeBean(BeanNode node, String attName, Class<?> type)
	{
		return new DynamicBean(typeConversationEnabled);
	}
	
}
