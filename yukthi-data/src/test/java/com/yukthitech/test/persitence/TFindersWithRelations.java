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
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.test.persitence.entity.Customer;
import com.yukthitech.test.persitence.entity.CustomerGroup;
import com.yukthitech.test.persitence.entity.ICustomerGroupRepository;
import com.yukthitech.test.persitence.entity.IOrderRepository;
import com.yukthitech.test.persitence.entity.Order;
import com.yukthitech.test.persitence.entity.OrderItem;
import com.yukthitech.persistence.GenericRepository;
import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.utils.CommonUtils;

public class TFindersWithRelations extends TestSuiteBase
{
	@Override
	protected void initFactoryBeforeClass(RepositoryFactory factory)
	{
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
		
		OrderItem item31 = new OrderItem("book", 20, null);
		
		Order order1 = new Order("order1", 10, null, Arrays.asList(item11, item12));
		Order order2 = new Order("order2", 20, null, Arrays.asList(item21, item22));
		Order order3 = new Order("order3", 30, null, Arrays.asList(item31));
		
		Customer customer1 = new Customer("Customer1", Arrays.asList(group1, group3), Arrays.asList(order1, order2));
		Customer customer2 = new Customer("Customer2", Arrays.asList(group1, group2), Arrays.asList(order3));
		
		genericRepository.save(customer1);
		genericRepository.save(customer2);
	}

	@Override
	protected void cleanFactoryAfterClass(RepositoryFactory factory)
	{
		//cleanup the tables
		factory.dropRepository(OrderItem.class);
		factory.dropRepository(Order.class);
		factory.dropRepository(Customer.class);
		factory.dropRepository(CustomerGroup.class);
	}
	
	/**
	 * Tests fetching entities with nested parent entity property as condition
	 * Also validates the sub entity proxies are set properly
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testWithParentRelation(RepositoryFactory factory)
	{
		IOrderRepository repo = factory.getRepository(IOrderRepository.class);
		
		List<Order> orders = repo.findOrdersOfCusomer("Customer1");
		Assert.assertEquals(orders.size(), 2);
		Assert.assertEquals(CommonUtils.toSet(orders.get(0).getTitle(), orders.get(1).getTitle()), CommonUtils.toSet("order1", "order2"));
		//Assert.assertEquals(orders.get(0).getItems().size(), 2);

		orders = repo.findOrdersOfCusomer("Customer2");
		Assert.assertEquals(orders.size(), 1);
		Assert.assertEquals(CommonUtils.toSet(orders.get(0).getTitle()), CommonUtils.toSet("order3"));

		//test if nested entity proxy works fine
		Assert.assertEquals(orders.get(0).getCustomer().getName(), "Customer2");
		
		orders = repo.findOrdersOfCusomer("Customerxxx");
		Assert.assertEquals(orders.size(), 0);
	}

	/**
	 * Tests fetching entities by using property of child item(s)
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testWithChildRelation(RepositoryFactory factory)
	{
		IOrderRepository repo = factory.getRepository(IOrderRepository.class);
		
		List<Order> orders = repo.findOrdersWithItem("soap");
		Assert.assertEquals(orders.size(), 2);
		Assert.assertEquals(CommonUtils.toSet(orders.get(0).getTitle(), orders.get(1).getTitle()), CommonUtils.toSet("order1", "order2"));

		orders = repo.findOrdersWithItem("book");
		Assert.assertEquals(orders.size(), 1);
		Assert.assertEquals(CommonUtils.toSet(orders.get(0).getTitle()), CommonUtils.toSet("order3"));

		orders = repo.findOrdersWithItem("nonexisting");
		Assert.assertEquals(orders.size(), 0);
	}

	/**
	 * Testes finding with nested relation and also which has UN-MAPPED relation via join table
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testWithNestedRelation(RepositoryFactory factory)
	{
		IOrderRepository repo = factory.getRepository(IOrderRepository.class);
		
		List<Order> orders = repo.findOrdersOfCusomerGroup("Group3");
		Assert.assertEquals(orders.size(), 2);
		Assert.assertEquals(CommonUtils.toSet(orders.get(0).getTitle(), orders.get(1).getTitle()), CommonUtils.toSet("order1", "order2"));

		orders = repo.findOrdersOfCusomerGroup("Group2");
		Assert.assertEquals(orders.size(), 1);
		Assert.assertEquals(CommonUtils.toSet(orders.get(0).getTitle()), CommonUtils.toSet("order3"));

		orders = repo.findOrdersOfCusomerGroup("unknown");
		Assert.assertEquals(orders.size(), 0);
	}

	/**
	 * Tests when expected return value involves relation
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testWithRelationResult(RepositoryFactory factory)
	{
		IOrderRepository repo = factory.getRepository(IOrderRepository.class);
		Assert.assertEquals(repo.findCustomerName(20), "Customer1");
	}
	
	@Test(dataProvider = "repositoryFactories")
	public void testWithMappedJoinRelation(RepositoryFactory factory)
	{
		ICustomerGroupRepository repo = factory.getRepository(ICustomerGroupRepository.class);
		
		List<CustomerGroup> groups = repo.findGroupsOfCusomer("Customer1");
		Assert.assertEquals(groups.size(), 2);
		Assert.assertEquals(CommonUtils.toSet(groups.get(0).getName(), groups.get(1).getName()), CommonUtils.toSet("Group1", "Group3"));
		
		groups = repo.findGroupsOfCusomer("Customer2");
		Assert.assertEquals(groups.size(), 2);
		Assert.assertEquals(CommonUtils.toSet(groups.get(0).getName(), groups.get(1).getName()), CommonUtils.toSet("Group1", "Group2"));

		groups = repo.findGroupsOfCusomer("nonexisting");
		Assert.assertEquals(groups.size(), 0);
	}

	@Test(dataProvider = "repositoryFactories")
	public void testManyToManyRelations(RepositoryFactory factory)
	{
		ICustomerGroupRepository repo = factory.getRepository(ICustomerGroupRepository.class);
		
		List<String> titles = repo.findOrderTitles("Group1");
		
		System.out.println(titles);
	}
}
