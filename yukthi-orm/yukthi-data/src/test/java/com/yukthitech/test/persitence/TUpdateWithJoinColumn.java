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
package com.yukthitech.test.persitence;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.test.persitence.entity.IOrderItemRepository;
import com.yukthitech.test.persitence.entity.IOrderRepository;
import com.yukthitech.test.persitence.entity.Order;
import com.yukthitech.test.persitence.entity.OrderItem;
import com.yukthitech.persistence.repository.RepositoryFactory;

/**
 * Ensures join columns can be used in update methods.
 * @author akiran
 */
public class TUpdateWithJoinColumn extends TestSuiteBase
{
	@Override
	protected void initFactoryBeforeClass(RepositoryFactory factory)
	{
	}

	@Override
	protected void cleanFactoryAfterClass(RepositoryFactory factory)
	{
		//cleanup the emp table
		factory.dropRepository(OrderItem.class);
		factory.dropRepository(Order.class);
	}

	/**
	 * Test join column full entity update
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testUpdateWithJoinColumn(RepositoryFactory factory)
	{
		IOrderRepository orderRepo = factory.getRepository(IOrderRepository.class);
		IOrderItemRepository itemRepo = factory.getRepository(IOrderItemRepository.class);

		Order order = new Order("Test", 123, null, null);
		orderRepo.save(order);
		
		OrderItem item = new OrderItem("item1", 10, order);
		itemRepo.save(item);
		
		item.setQuantity(20);
		Assert.assertTrue(itemRepo.updateForOrder(item, order.getId(), "item1"));
		
		Assert.assertTrue(itemRepo.updateQuantityForOrder(order.getId(), "item1", 30));
		Assert.assertFalse(itemRepo.updateQuantityForOrder(order.getId(), "item2", 30));
	}
}
