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
package com.yukthitech.utils.doc;

import java.util.List;

/**
 * Base class for all documentations.
 * @author akranthikiran
 */
public class BaseDoc<T extends BaseDoc<T>>
{
	/**
	 * Name of the element.
	 */
	private String name;
	
	/**
	 * Description of the element.
	 */
	private String description;
	
	/**
	 * Examples.
	 */
	private List<String> examples;
	
	/**
	 * Group of the element.
	 */
	private String group;

	public String getName()
	{
		return name;
	}

	@SuppressWarnings("unchecked")
	public T setName(String name)
	{
		this.name = name;
		return (T) this;
	}

	public String getDescription()
	{
		return description;
	}

	@SuppressWarnings("unchecked")
	public T setDescription(String description)
	{
		this.description = description;
		return (T) this;
	}

	public List<String> getExamples()
	{
		return examples;
	}

	@SuppressWarnings("unchecked")
	public T setExamples(List<String> examples)
	{
		this.examples = examples;
		return (T) this;
	}

	public String getGroup()
	{
		return group;
	}

	@SuppressWarnings("unchecked")
	public T setGroup(String group)
	{
		this.group = group;
		return (T) this;
	}
}
