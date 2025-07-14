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
package com.yukthitech.test.persitence.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.ForeignConstraintMessage;

@Table(name = "ORDER_ITEM")
public class OrderItem
{
	public static final String FK_INVALID_ORDER_MSG = "No order exist with specified id";
	public static final String FK_ORDER_DEL_MSG = "No order exist with specified id";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column
	private String itemName;

	@Column
	private int quantity;
	
	@ForeignConstraintMessage(message = FK_INVALID_ORDER_MSG, deleteMessage = FK_ORDER_DEL_MSG)
	@ManyToOne
	@Column(name = "ORDER_ID")
	private Order order;

	public OrderItem()
	{}
	
	public OrderItem(String itemName, int quantity, Order order)
	{
		this.itemName = itemName;
		this.quantity = quantity;
		this.order = order;
	}
	
	/**
	 * @return the {@link #itemName itemName}
	 */
	public String getItemName()
	{
		return itemName;
	}

	/**
	 * @param itemName the {@link #itemName itemName} to set
	 */
	public void setItemName(String itemName)
	{
		this.itemName = itemName;
	}

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
	 * @return the {@link #quantity quantity}
	 */
	public int getQuantity()
	{
		return quantity;
	}

	/**
	 * @param quantity the {@link #quantity quantity} to set
	 */
	public void setQuantity(int quantity)
	{
		this.quantity = quantity;
	}

	/**
	 * @return the {@link #order order}
	 */
	public Order getOrder()
	{
		return order;
	}

	/**
	 * @param order the {@link #order order} to set
	 */
	public void setOrder(Order order)
	{
		this.order = order;
	}
	
	
}
