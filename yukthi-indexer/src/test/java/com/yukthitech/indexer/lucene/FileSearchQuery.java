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
package com.yukthitech.indexer.lucene;

import java.util.Set;

import com.yukthitech.indexer.search.SearchCondition;

public class FileSearchQuery
{
	@SearchCondition
	private Set<String> nameParts;
	
	@SearchCondition
	private String fileName;
	
	@SearchCondition
	private String content;

	public FileSearchQuery(Set<String> nameParts)
	{
		this.nameParts = nameParts;
	}

	public FileSearchQuery(String fileName, String content)
	{
		this.fileName = fileName;
		this.content = content;
	}
}
