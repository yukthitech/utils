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

import java.util.Collections;

public abstract class BeanFactory
{
	public static final Object SKIP_TO_NORMAL = new Object();

	public Object createBean(BeanNode node)
	{
		return DefaultParserHandler.createBean(node, (ClassLoader) null, Collections.<String, Object>emptyMap());
	}

	public Object createBean(BeanNode node, ClassLoader loader)
	{
		return DefaultParserHandler.createBean(node, loader, Collections.<String, Object>emptyMap());
	}

	public abstract Object buildBean(Class<?> preferredType, BeanNode node);

	public abstract Object buildAttributeBean(Class<?> preferredType, BeanNode node, String attName);

	public void finalize()
	{}
}
