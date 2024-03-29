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
package com.yukthitech.autox.filter;

import com.yukthitech.persistence.UnsupportedOperationException;

/**
 * Represents a path that can be used to get or set value.
 * @author akiran
 */
public interface IPropertyPath
{
	/**
	 * Fetches the value of the current path.
	 * @return value of the path
	 */
	public Object getValue() throws Exception;
	
	/**
	 * Sets the value of the current path.
	 * @param value value to set.
	 */
	public default void setValue(Object value) throws Exception
	{
		throw new UnsupportedOperationException("Write is not supported with this expression type");
	}

	public default void removeValue() throws Exception
	{
		throw new UnsupportedOperationException("Remove is not supported with this expression type");
	}
}
