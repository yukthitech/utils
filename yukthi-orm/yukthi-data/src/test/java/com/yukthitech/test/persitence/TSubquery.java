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

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.test.persitence.entity.Customer;
import com.yukthitech.test.persitence.entity.CustomerGroup;
import com.yukthitech.test.persitence.entity.IOrderItemRepository;
import com.yukthitech.test.persitence.entity.Order;
import com.yukthitech.test.persitence.entity.OrderItem;
import com.yukthitech.persistence.GenericRepository;
import com.yukthitech.persistence.repository.RepositoryFactory;

public class TSubquery extends TestSuiteBase
{
	@Override
	protected void initFactoryBeforeClass(RepositoryFactory factory)
	{
		//this ensures all child classes are created as required before insert is started
		factory.getRepository(IOrderItemRepository.class);

		GenericRepository genericRepository = new GenericRepository(factory);
		
		CustomerGroup group1 = new CustomerGroup("Group1", null);
		CustomerGroup group2 = new CustomerGroup("Group2", null);
		CustomerGroup group3 = new CustomerGroup("Group3", null);

		genericRepository.save(group1);
		genericRepository.save(group2);
		genericRepository.save(group3);
		
		OrderItem item11 = new OrderItem("soap", 10, null);
		OrderItem item12 = new OrderItem("box", 20, null);
		
		OrderItem item21 = new OrderItem("soap", 10, null);
		OrderItem item22 = new OrderItem("brush", 3, null);
		OrderItem item23 = new OrderItem("brush1", 4, null);
		OrderItem item24 = new OrderItem("brush2", 5, null);
		
		OrderItem item31 = new OrderItem("book", 20, null);
		OrderItem item32 = new OrderItem("brush3", 4, null);
		OrderItem item33 = new OrderItem("brush4", 5, null);
		
		OrderItem item41 = new OrderItem("book", 20, null);
		OrderItem item42 = new OrderItem("brush3", 4, null);

		Order order1 = new Order("order1", 10, null, Arrays.asList(item11, item12));
		Order order2 = new Order("order2", 20, null, Arrays.asList(item21, item22, item23, item24));
		Order order3 = new Order("order3", 30, null, Arrays.asList(item31, item32, item33));
		Order order4 = new Order("order4", 40, null, Arrays.asList(item41, item42));
		
		Customer customer1 = new Customer("Customer1", Arrays.asList(group1, group3), Arrays.asList(order1, order2));
		Customer customer2 = new Customer("Customer2", Arrays.asList(group1, group2), Arrays.asList(order3));
		Customer customer3 = new Customer("Customer3", Arrays.asList(group1, group2), Arrays.asList(order4));
		
		genericRepository.save(customer1);
		genericRepository.save(customer2);
		genericRepository.save(customer3);
	}

	@Override
	protected void cleanFactory(RepositoryFactory factory)
	{
		//cleanup the tables
		factory.dropRepository(OrderItem.class);
		factory.dropRepository(Order.class);
		factory.dropRepository(Customer.class);
		factory.dropRepository(CustomerGroup.class);
	}
	
	/**
	 * Tests delete with one level sub query.
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testDeleteWithOneLevelRelation(RepositoryFactory factory)
	{
		IOrderItemRepository repo = factory.getRepository(IOrderItemRepository.class);
		int count = repo.deleteByOrderName(5, "order3");
		
		Assert.assertEquals(count, 1);
	}

	/**
	 * Tests delete with multi level sub query.
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testDeleteWithTwoLevelRelation(RepositoryFactory factory)
	{
		IOrderItemRepository repo = factory.getRepository(IOrderItemRepository.class);
		int count = repo.deleteByCustomerName(5, "Customer1");
		
		Assert.assertEquals(count, 1);
	}
	
	@Test(dataProvider = "repositoryFactories")
	public void testUpdateWithSubquery(RepositoryFactory factory)
	{
		IOrderItemRepository repo = factory.getRepository(IOrderItemRepository.class);
		int count = repo.updateByCustomerName(2, "Customer3");
		
		Assert.assertEquals(count, 2);
	}
}
