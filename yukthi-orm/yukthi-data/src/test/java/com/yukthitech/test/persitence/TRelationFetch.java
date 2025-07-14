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
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.persistence.GenericRepository;
import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.test.persitence.entity.Customer;
import com.yukthitech.test.persitence.entity.CustomerAddress;
import com.yukthitech.test.persitence.entity.CustomerGroup;
import com.yukthitech.test.persitence.entity.ICustomerAddressRepository;
import com.yukthitech.test.persitence.entity.ICustomerGroupRepository;
import com.yukthitech.test.persitence.entity.ICustomerRepository;
import com.yukthitech.test.persitence.entity.IOrderRepository;
import com.yukthitech.test.persitence.entity.Order;
import com.yukthitech.test.persitence.entity.OrderItem;
import com.yukthitech.utils.CommonUtils;

/**
 * Ensures the subrelation entities are fetched properly.
 * @author akiran
 */
public class TRelationFetch extends TestSuiteBase
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
		customer1.setAddress(new CustomerAddress("add1", "city", "state"));
		
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
		factory.dropRepository(CustomerAddress.class);
		factory.dropRepository(CustomerGroup.class);
	}
	
	/**
	 * Ensures fetching of self-owned entity is working as expected.
	 * 		Customer from Order
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testOwnedRelationFetch(RepositoryFactory factory)
	{
		IOrderRepository repo = factory.getRepository(IOrderRepository.class);
		
		Order order = repo.findOrderByOrderNo(10);
		Assert.assertEquals(order.getCustomer().getName(), "Customer1");
	}

	/**
	 * Ensures fetching of mapped entity is working as expected.
	 * 		Customer from customer-address
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testMappedRelationFetch(RepositoryFactory factory)
	{
		ICustomerAddressRepository repo = factory.getRepository(ICustomerAddressRepository.class);
		
		CustomerAddress address = repo.findByPropertyId("add1");
		Assert.assertEquals(address.getCustomer().getName(), "Customer1");
	}

	/**
	 * Ensures fetching of directly-mapped multi-valued child entities field is working as expected.
	 * 		Items from Order
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testMappedMultiValuedFetch(RepositoryFactory factory)
	{
		IOrderRepository repo = factory.getRepository(IOrderRepository.class);
		
		Order order = repo.findOrderByOrderNo(10);
		
		//Ensure multi valued field is fetched properly
		Assert.assertEquals(order.getItems().size(), 2);
		
		Set<String> itemNames = order.getItems().stream()
				.map(item -> item.getItemName())
				.collect(Collectors.toSet());
		
		Assert.assertEquals(itemNames, CommonUtils.toSet("soap", "box"));
	}

	/**
	 * Ensures fetching of join table multi-valued child entities field is working as expected.
	 * 		Customer-groups from customer
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testJoinedMultiValuedFetch(RepositoryFactory factory)
	{
		ICustomerRepository repo = factory.getRepository(ICustomerRepository.class);

		Customer customer = repo.findByName("Customer1");
		
		//Ensure multi valued field is fetched properly
		Assert.assertEquals(customer.getCustomerGroups().size(), 2);
		
		Set<String> groupNames = customer.getCustomerGroups().stream()
				.map(grp -> grp.getName())
				.collect(Collectors.toSet());
		
		Assert.assertEquals(groupNames, CommonUtils.toSet("Group1", "Group3"));
	}

	/**
	 * Ensures fetching of join table multi-valued child entities mapped-field is working as expected.
	 * 		customers from customer-group
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testMappedJoinedMultiValuedFetch(RepositoryFactory factory)
	{
		ICustomerGroupRepository repo = factory.getRepository(ICustomerGroupRepository.class);

		CustomerGroup customerGroup = repo.findByName("Group1");
		
		//Ensure multi valued field is fetched properly
		Assert.assertEquals(customerGroup.getCustomers().size(), 2);
		
		Set<String> customerNames = customerGroup.getCustomers().stream()
				.map(grp -> grp.getName())
				.collect(Collectors.toSet());
		
		Assert.assertEquals(customerNames, CommonUtils.toSet("Customer1", "Customer2"));
	}
	
	/**
	 * Ensures fetching of recursive relation fetch based on single valued field is working.
	 * 		Customer from customer-address
	 * 		Orders from customer
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testRecursiveFetch_SingleValued(RepositoryFactory factory)
	{
		ICustomerAddressRepository repo = factory.getRepository(ICustomerAddressRepository.class);
		
		CustomerAddress address = repo.findByPropertyId("add1");
		Assert.assertEquals(address.getCustomer().getName(), "Customer1");
		
		List<Order> orders = address.getCustomer().getOrders();
		Assert.assertEquals(orders.size(), 2);

		Set<String> orderNames = orders.stream()
				.map(order -> order.getTitle())
				.collect(Collectors.toSet());

		Assert.assertEquals(orderNames, CommonUtils.toSet("order1", "order2"));
	}

	/**
	 * Ensures fetching of recursive relation fetch based on multi valued field is working.
	 * 		Items from Order
	 * 		order from items
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testRecursiveFetch_multiValue(RepositoryFactory factory)
	{
		IOrderRepository repo = factory.getRepository(IOrderRepository.class);
		
		Order order = repo.findOrderByOrderNo(10);
		
		//Ensure multi valued field is fetched properly
		Assert.assertEquals(order.getItems().size(), 2);
		Assert.assertEquals(order.getItems().get(0).getOrder().getOrderNo(), 10);
	}

	/**
	 * Ensures fetching of recursive relation fetch based on multi valued field is working.
	 * 		Customer-groups from customer
	 * 		customers from customer-groups
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testRecursiveFetch_joinedMultiValue(RepositoryFactory factory)
	{
		ICustomerRepository repo = factory.getRepository(ICustomerRepository.class);

		Customer customer = repo.findByName("Customer1");
		
		//Ensure multi valued field is fetched properly
		Assert.assertEquals(customer.getCustomerGroups().size(), 2);
		
		CustomerGroup group1 = customer.getCustomerGroups().get(0).getName().equals("Group1") ? customer.getCustomerGroups().get(0) : customer.getCustomerGroups().get(1); 
		
		Set<String> groupNames = group1.getCustomers().stream()
				.map(cust -> cust.getName())
				.collect(Collectors.toSet());
		
		Assert.assertEquals(groupNames, CommonUtils.toSet("Customer1", "Customer2"));
	}
}
