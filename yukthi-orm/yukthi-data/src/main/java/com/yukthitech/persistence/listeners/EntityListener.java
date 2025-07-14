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
package com.yukthitech.persistence.listeners;

import java.lang.reflect.Method;

/**
 * Holds listener method details for event handling
 * @author akiran
 */
public class EntityListener
{
	/**
	 * Object containing listener method
	 */
	private Object listenerContainer;
	
	/**
	 * Listener method
	 */
	private Method listenerMethod;
	
	/**
	 * Does listener method accept event object
	 */
	private boolean hasEventArg;

	public EntityListener(Object listenerContainer, Method listenerMethod, boolean hasEventArg)
	{
		this.listenerContainer = listenerContainer;
		this.listenerMethod = listenerMethod;
		this.hasEventArg = hasEventArg;
	}
	
	/**
	 * Invokes the listener method with specified event object
	 * @param e
	 */
	public void invoke(EntityEvent e)
	{
		try
		{
			if(hasEventArg)
			{
				listenerMethod.invoke(listenerContainer, e);
				return;
			}
			
			listenerMethod.invoke(listenerContainer);
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while invoking ");
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		
		builder.append("[");
		builder.append(listenerContainer.getClass().getName()).append(".").append(listenerMethod.getName()).append("()");
		builder.append("]");
		
		return builder.toString();
	}

}
