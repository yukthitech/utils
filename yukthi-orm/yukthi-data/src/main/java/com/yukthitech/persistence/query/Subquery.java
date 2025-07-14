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

import java.util.List;

import com.yukthitech.persistence.EntityDetails;

/**
 * Sub query that gets embedded in the main query.
 * @author akiran
 */
public class Subquery extends AbstractConditionalQuery
{
	/**
	 * Table code to be used for main table.
	 */
	private String mainTableCode;
	
	/**
	 * Instantiates a new subquery.
	 *
	 * @param entityDetails the entity details
	 * @param tableCode the table code
	 */
	public Subquery(EntityDetails entityDetails, String tableCode)
	{
		super(entityDetails);
		this.mainTableCode = tableCode;
	}

	/**
	 * Gets the table code to be used for main table.
	 *
	 * @return the table code to be used for main table
	 */
	public String getMainTableCode()
	{
		return mainTableCode;
	}

	/**
	 * Populates the parameters required conditions of this sub query.
	 * @param params List to which parameters needs to be populated.
	 */
	public void fetchQueryParameters(List<Object> params)
	{
		for(QueryCondition condition : super.getConditions())
		{
			condition.fetchQueryParameters(params);
		}
	}
}
