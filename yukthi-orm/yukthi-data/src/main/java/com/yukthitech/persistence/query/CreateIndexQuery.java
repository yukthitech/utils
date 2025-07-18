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
package com.yukthitech.persistence.query;

import java.util.Arrays;

import com.yukthitech.persistence.EntityDetails;

public class CreateIndexQuery extends Query
{
	private String indexName;
	private String columns[];
	
	public CreateIndexQuery(EntityDetails entityDetails, String indexName, String columns[])
	{
		super(entityDetails);

		this.indexName = indexName;
		this.columns = columns;
	}
	
	public String getIndexName()
	{
		return indexName;
	}
	
	public String[] getColumns()
	{
		return columns;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(getClass().getName()).append("[Name: ");
		
		builder.append(indexName).append(", Columns: ").append(Arrays.toString(columns));
		
		builder.append("]");
		
		return builder.toString();
	}
}
