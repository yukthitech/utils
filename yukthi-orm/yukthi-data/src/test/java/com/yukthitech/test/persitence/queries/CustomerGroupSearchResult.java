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
import com.yukthitech.test.persitence.entity.Customer;

public class CustomerGroupSearchResult
{
	@Field("id")
	private long id;

	@Field("name")
	private String name;
	
	@Field("customers")
	private List<Customer> customers;

	public CustomerGroupSearchResult()
	{}
	
	/**
	 * @return the {@link #id id}
	 */
	public long getId()
	{
		return id;
	}

	/**
	 * @param id the {@link #id id} to set
	 */
	public void setId(long id)
	{
		this.id = id;
	}

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
	 * @return the {@link #customers customers}
	 */
	public List<Customer> getCustomers()
	{
		return customers;
	}

	/**
	 * @param customers the {@link #customers customers} to set
	 */
	public void setCustomers(List<Customer> customers)
	{
		this.customers = customers;
	}
	
	
}
