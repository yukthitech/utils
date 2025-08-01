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
import com.yukthitech.test.persitence.entity.OrderItem;

public class OrderSearchResult
{
	@Field("title")
	private String title;
	
	@Field("orderNo")
	private int orderNo;
	
	@Field("customer")
	private Customer customer;
	
	@Field("items")
	private List<OrderItem> items;
	
	@Field("cost")
	private float cost;
	
	public OrderSearchResult()
	{}

	/**
	 * @return the {@link #title title}
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * @param title the {@link #title title} to set
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * @return the {@link #orderNo orderNo}
	 */
	public int getOrderNo()
	{
		return orderNo;
	}

	/**
	 * @param orderNo the {@link #orderNo orderNo} to set
	 */
	public void setOrderNo(int orderNo)
	{
		this.orderNo = orderNo;
	}

	/**
	 * @return the {@link #customer customer}
	 */
	public Customer getCustomer()
	{
		return customer;
	}

	/**
	 * @param customer the {@link #customer customer} to set
	 */
	public void setCustomer(Customer customer)
	{
		this.customer = customer;
	}

	/**
	 * @return the {@link #items items}
	 */
	public List<OrderItem> getItems()
	{
		return items;
	}

	/**
	 * @param items the {@link #items items} to set
	 */
	public void setItems(List<OrderItem> items)
	{
		this.items = items;
	}

	public float getCost()
	{
		return cost;
	}

	public void setCost(float cost)
	{
		this.cost = cost;
	}
}
