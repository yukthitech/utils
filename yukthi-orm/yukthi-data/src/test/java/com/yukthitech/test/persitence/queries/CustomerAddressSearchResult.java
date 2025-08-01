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

import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.test.persitence.entity.Customer;

/**
 * Sub bean for employee1
 * @author akiran
 *
 */
public class CustomerAddressSearchResult
{
	@Field("id")
	private Long id;
	
	@Field("city")
	private String city;

	@Field("state")
	private String state;
	
	@Field("customer")
	private Customer customer;
	
	public CustomerAddressSearchResult()
	{}

	/**
	 * @return the {@link #city city}
	 */
	public String getCity()
	{
		return city;
	}

	/**
	 * @param city the {@link #city city} to set
	 */
	public void setCity(String city)
	{
		this.city = city;
	}

	/**
	 * @return the {@link #state state}
	 */
	public String getState()
	{
		return state;
	}

	/**
	 * @param state the {@link #state state} to set
	 */
	public void setState(String state)
	{
		this.state = state;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Customer getCustomer()
	{
		return customer;
	}

	public void setCustomer(Customer customer)
	{
		this.customer = customer;
	}
}
