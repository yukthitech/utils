package com.yukthitech.ccg.xml;

import java.util.ArrayList;

import com.yukthitech.ccg.xml.util.TypeConversionUtils;

/**
 * Used to accept list of dynamic beans.
 * @author akranthikiran
 */
public class DynamicBeanList extends ArrayList<Object>
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * If this flag enabled, based on prefix values will be type converted.
	 */
	private boolean typeConversationEnabled;

	public DynamicBeanList(boolean typeConversationEnabled)
	{
		this.typeConversationEnabled = typeConversationEnabled;
	}

	public void addElement(Object obj)
	{
		if(typeConversationEnabled)
		{
			obj = TypeConversionUtils.strToObject(obj);
		}
		
		super.add(obj);
	}
}
