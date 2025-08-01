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
package com.yukthitech.test.persitence.queries;

import java.util.List;

import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.test.persitence.entity.CustomerAddress;
import com.yukthitech.test.persitence.entity.CustomerGroup;
import com.yukthitech.test.persitence.entity.Order;

public class CustomerSearchResult
{
	@Field("name")
	private String name;

	@Field("customerGroups")
	private List<CustomerGroup> customerGroups;

	@Field("orders")
	private List<Order> orders;
	
	@Field("address")
	private CustomerAddress address;

	public CustomerSearchResult()
	{}
	
	/**
	 * @return the {@link #name name}
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the {@link #name name} to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the {@link #customerGroups customerGroups}
	 */
	public List<CustomerGroup> getCustomerGroups()
	{
		return customerGroups;
	}

	/**
	 * @param customerGroups the {@link #customerGroups customerGroups} to set
	 */
	public void setCustomerGroups(List<CustomerGroup> customerGroups)
	{
		this.customerGroups = customerGroups;
	}

	/**
	 * @return the {@link #orders orders}
	 */
	public List<Order> getOrders()
	{
		return orders;
	}

	/**
	 * @param orders the {@link #orders orders} to set
	 */
	public void setOrders(List<Order> orders)
	{
		this.orders = orders;
	}

	public CustomerAddress getAddress()
	{
		return address;
	}

	public void setAddress(CustomerAddress address)
	{
		this.address = address;
	}
}
