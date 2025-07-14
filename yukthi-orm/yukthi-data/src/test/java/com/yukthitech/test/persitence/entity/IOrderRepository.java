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

import java.util.List;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.AggregateFunction;
import com.yukthitech.persistence.repository.annotations.AggregateFunctionType;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Field;

public interface IOrderRepository extends ICrudRepository<Order>
{
	public List<Order> findOrdersWithItem(@Condition("items.itemName") String itemName);
	
	public List<Order> findOrdersOfCusomer(@Condition("customer.name") String customerName);
	
	public List<Order> findOrdersOfCusomerGroup(@Condition("customer.customerGroups.name") String customerGroupName);

	@Field("customer.name")
	public String findCustomerName(@Condition("orderNo") int orderNo);
	
	public Order findOrderByOrderNo(int orderNo);
	
	@AggregateFunction(type = AggregateFunctionType.COUNT)
	public int getOrderCount(@Condition("customer.name") String customerName);

	@AggregateFunction(type = AggregateFunctionType.MAX, field = "cost")
	public float getMaxCost(@Condition("customer.name") String customerName);

	@AggregateFunction(type = AggregateFunctionType.AVG, field = "cost")
	public float getAverageCost(@Condition("customer.name") String customerName);

	@AggregateFunction(type = AggregateFunctionType.MIN, field = "cost")
	public float getMinCost(@Condition("customer.name") String customerName);
}
