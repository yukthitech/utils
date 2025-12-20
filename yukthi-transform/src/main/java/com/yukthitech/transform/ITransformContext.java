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
package com.yukthitech.transform;

import java.util.Set;
import java.util.Map.Entry;

/**
 * Abstraction of context object which can allow setting the value along with other property
 * read access.
 * 
 * @author Kranthi
 */
public interface ITransformContext
{
	public Set<Entry<String, Object>> entrySet();
	
	public Object get(Object key);
	
	/**
	 * Sets the value of with specified key on the context.
	 * @param key key to use
	 * @param value value to set
	 */
	public void setValue(String key, Object value);
}
