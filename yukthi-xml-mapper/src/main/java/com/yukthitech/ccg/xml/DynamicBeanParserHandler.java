/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
		return new DynamicBean(node.getName(), typeConversationEnabled);
	}

	@Override
	public Object createBean(BeanNode node, XMLAttributeMap att, ClassLoader loader)
	{
		if(DynamicBeanList.class.equals(node.getActualType()))
		{
			return new DynamicBeanList(typeConversationEnabled);
		}
		
		return new DynamicBean(node.getName(), typeConversationEnabled);
	}

	@Override
	public Object parseAttributeValue(BeanNode node, String attName, Class<?> type)
	{
		return new DynamicBean(attName, typeConversationEnabled);
	}
	
}
