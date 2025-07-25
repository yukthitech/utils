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

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.ExtendedTableDetails;

/**
 * Query to create extended table.
 * @author akiran
 */
public class CreateExtendedTableQuery extends Query
{
	private ExtendedTableDetails extendedTableDetails;

	public CreateExtendedTableQuery(EntityDetails entityDetails, ExtendedTableDetails extendedTableDetails)
	{
		super(entityDetails);
		this.extendedTableDetails = extendedTableDetails;
	}
	
	/**
	 * @return the {@link #extendedTableDetails extendedTableDetails}
	 */
	public ExtendedTableDetails getExtendedTableDetails()
	{
		return extendedTableDetails;
	}
}
