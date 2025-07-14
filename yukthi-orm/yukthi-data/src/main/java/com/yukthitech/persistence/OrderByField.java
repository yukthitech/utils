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
package com.yukthitech.persistence;

import com.yukthitech.persistence.repository.annotations.OrderByType;

/**
 * Order by clause field details.
 * @author akiran
 */
public class OrderByField
{
	/**
	 * Name of the order field.
	 */
	private String name;
	
	/**
	 * Order by type.
	 */
	private OrderByType orderByType;

	/**
	 * Instantiates a new order field.
	 *
	 * @param name the name
	 * @param orderByType the order by type
	 */
	public OrderByField(String name, OrderByType orderByType)
	{
		this.name = name;
		this.orderByType = orderByType;
	}

	/**
	 * Gets the name of the order field.
	 *
	 * @return the name of the order field
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the order field.
	 *
	 * @param name the new name of the order field
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the order by type.
	 *
	 * @return the order by type
	 */
	public OrderByType getOrderByType()
	{
		return orderByType;
	}

	/**
	 * Sets the order by type.
	 *
	 * @param orderByType the new order by type
	 */
	public void setOrderByType(OrderByType orderByType)
	{
		this.orderByType = orderByType;
	}
}
