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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.yukthitech.persistence.ForeignConstraintViolationException;
import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.UniqueConstraintViolationException;
import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.test.persitence.entity.Employee;
import com.yukthitech.test.persitence.entity.IEmployeeRepository;
import com.yukthitech.test.persitence.entity.IOrderRepository;
import com.yukthitech.test.persitence.entity.Order;
import com.yukthitech.test.persitence.entity.OrderItem;

/**
 * @author akiran
 *
 */
public class TConstraintExceptionHandling extends TestSuiteBase
{
	private static Logger logger = LogManager.getLogger(TConstraintExceptionHandling.class);
	
	@AfterMethod
	public void cleanup(ITestResult result)
	{
		Object params[] = result.getParameters();
		RepositoryFactory factory = (RepositoryFactory)params[0];
		
		//cleanup the emp table
		factory.dropRepository(Employee.class);
		factory.dropRepository(OrderItem.class);
		factory.dropRepository(Order.class);
	}

	@Test(dataProvider = "repositoryFactories")
	public void testUniqueConstraintViolation_create(RepositoryFactory factory)
	{
		IEmployeeRepository empRepository = factory.getRepository(IEmployeeRepository.class);
		
		Employee emp = new Employee("12345", "kranthi@kk.com", "kranthi", "90232333", 28);
		empRepository.save(emp);

		Employee emp1 = new Employee("123453", "kranthi@kk.com", "kranthi1", "902323331", 29);

		try
		{
			empRepository.save(emp1);
			
			Assert.fail("Unique constraint exception is not thrown");
		}catch(Exception ex)
		{
			logger.debug("Exception occurred during unique constraint check is", ex);
			
			Assert.assertTrue(ex instanceof UniqueConstraintViolationException);
			Assert.assertEquals(Employee.ERROR_MESSAGE_DUPLICATE_EMAIL, ex.getMessage());
		}
		
		empRepository.deleteById(emp.getId());
		Assert.assertTrue(empRepository.save(emp1));
	}

	@Test(dataProvider = "repositoryFactories")
	public void testUniqueConstraintViolation_update(RepositoryFactory factory)
	{
		IEmployeeRepository empRepository = factory.getRepository(IEmployeeRepository.class);
		
		Employee emp = new Employee("12345", "kranthi@kk.com", "kranthi", "90232333", 28);
		empRepository.save(emp);

		Employee emp1 = new Employee("123453", "kranthi1@kk.com", "kranthi1", "902323331", 29);
		empRepository.save(emp1);

		try
		{
			Employee empToUpdate = emp1;
			empToUpdate.setEmailId("kranthi@kk.com");

			empRepository.update(empToUpdate);
			Assert.fail("Unique constraint exception is not thrown");
		}catch(Exception ex)
		{
			Assert.assertTrue(ex instanceof UniqueConstraintViolationException);
			Assert.assertEquals(Employee.ERROR_MESSAGE_DUPLICATE_EMAIL, ex.getMessage());
		}
	}
	
	@Test(dataProvider = "repositoryFactories")
	public void testForeignConstraintViolation_create(RepositoryFactory factory)
	{
		ICrudRepository<OrderItem> itemRepository = factory.getRepositoryForEntity(OrderItem.class);

		OrderItem item = new OrderItem("item1", 1, new Order(10000));
		
		try
		{
			itemRepository.save(item);
			
			Assert.fail("Foreign constraint exception is not thrown");
		}catch(Exception ex)
		{
			Assert.assertTrue(ex instanceof ForeignConstraintViolationException);
			Assert.assertEquals(OrderItem.FK_INVALID_ORDER_MSG, ex.getMessage());
		}
	}
	
	@Test(dataProvider = "repositoryFactories")
	public void testForeignConstraintViolation_update(RepositoryFactory factory)
	{
		IOrderRepository orderRepository = factory.getRepository(IOrderRepository.class);
		ICrudRepository<OrderItem> itemRepository = factory.getRepositoryForEntity(OrderItem.class);

		Order order = new Order("order", 123, null, null);
		OrderItem item = new OrderItem("item1", 1, order);
		
		orderRepository.save(order);
		itemRepository.save(item);
		
		try
		{
			item.setOrder(new Order(2000));
			itemRepository.save(item);
			
			Assert.fail("Foreign constraint exception is not thrown");
		}catch(Exception ex)
		{
			Assert.assertTrue(ex instanceof ForeignConstraintViolationException);
			Assert.assertEquals(OrderItem.FK_INVALID_ORDER_MSG, ex.getMessage());
		}
	}

	@Test(dataProvider = "repositoryFactories")
	public void testForeignConstraintViolation_delete(RepositoryFactory factory)
	{
		IOrderRepository orderRepository = factory.getRepository(IOrderRepository.class);
		ICrudRepository<OrderItem> itemRepository = factory.getRepositoryForEntity(OrderItem.class);

		Order order = new Order("order", 123, null, null);
		OrderItem item = new OrderItem("item1", 1, order);
		
		orderRepository.save(order);
		itemRepository.save(item);
		
		try
		{
			orderRepository.deleteById(order.getId());
			
			Assert.fail("Foreign constraint exception is not thrown");
		}catch(Exception ex)
		{
			Assert.assertTrue(ex instanceof ForeignConstraintViolationException);
			Assert.assertEquals(OrderItem.FK_ORDER_DEL_MSG, ex.getMessage());
		}
	}
}
