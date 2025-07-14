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

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.persistence.repository.annotations.UpdateOperator;

public interface IOrderItemRepository extends ICrudRepository<OrderItem>
{
	public boolean updateForOrder(OrderItem orderItem, @Condition("order.id") Long orderId, @Condition("itemName") String name);
	
	public boolean updateQuantityForOrder(@Condition("order.id") Long orderId, @Condition("itemName") String name, @Field("quantity") int quantity);
	
	public int deleteByOrderName(@Condition("quantity") int quantity, @Condition("order.title") String orderName);
	
	public int deleteByCustomerName(@Condition("quantity") int quantity, @Condition("order.customer.name") String customerName);
	
	public int updateByCustomerName(@Field(value = "quantity", updateOp = UpdateOperator.ADD) int quantity, @Condition("order.customer.name") String customerName);
}
