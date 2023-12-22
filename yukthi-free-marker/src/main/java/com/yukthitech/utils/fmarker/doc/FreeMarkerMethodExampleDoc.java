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
package com.yukthitech.utils.fmarker.doc;

import com.yukthitech.utils.fmarker.annotaion.ExampleDoc;

/**
 * Represents method example documentation.
 * @author akiran
 */
public class FreeMarkerMethodExampleDoc
{
	/**
	 * Title for this example.
	 */
	private String title;
	
	/**
	 * Usage example.
	 */
	private String usage;
	
	/**
	 * Result of the usage.
	 */
	private String result;
	
	/**
	 * Instantiates a new free marker method example doc.
	 */
	public FreeMarkerMethodExampleDoc()
	{}
	
	/**
	 * Instantiates a new free marker method example doc.
	 *
	 * @param doc the doc
	 */
	public FreeMarkerMethodExampleDoc(ExampleDoc doc)
	{
		this.title = doc.title();
		this.usage = doc.usage();
		this.result = doc.result();
	}
	
	public String getTitle()
	{
		return title;
	}

	/**
	 * Gets the usage example.
	 *
	 * @return the usage example
	 */
	public String getUsage()
	{
		return usage;
	}

	/**
	 * Gets the result of the usage.
	 *
	 * @return the result of the usage
	 */
	public String getResult()
	{
		return result;
	}
}
