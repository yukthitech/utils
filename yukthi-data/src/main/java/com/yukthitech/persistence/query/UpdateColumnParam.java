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

import com.yukthitech.persistence.repository.annotations.UpdateOperator;

/**
 * Column parameter details for update query
 * @author akiran
 */
public class UpdateColumnParam extends ColumnParam
{
	/**
	 * Update operator to be used
	 */
	private UpdateOperator updateOperator;
	
	/**
	 * Instantiates a new update column param.
	 *
	 * @param name the name
	 * @param value the value
	 * @param index the index
	 * @param updateOp the update op
	 */
	public UpdateColumnParam(String name, Object value, int index, UpdateOperator updateOp)
	{
		super(name, value, index);
		this.updateOperator = updateOp;
	}

	public UpdateColumnParam(String name, Object value, int index, String fieldName, UpdateOperator updateOp)
	{
		super(name, value, index);
		this.updateOperator = updateOp;
		super.setFieldName(fieldName);
	}

	/**
	 * Gets the update operator to be used.
	 *
	 * @return the update operator to be used
	 */
	public UpdateOperator getUpdateOperator()
	{
		return updateOperator;
	}

	/**
	 * Sets the update operator to be used.
	 *
	 * @param updateOperator the new update operator to be used
	 */
	public void setUpdateOperator(UpdateOperator updateOperator)
	{
		this.updateOperator = updateOperator;
	}
}
