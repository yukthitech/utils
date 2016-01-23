/*
 * The MIT License (MIT)
 * Copyright (c) 2016 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.fw.test.persitence;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.fw.test.persitence.entity.Employee;
import com.fw.test.persitence.entity.IEmployeeRepository;
import com.fw.test.persitence.entity.IOrderRepository;
import com.fw.test.persitence.entity.Order;
import com.fw.test.persitence.entity.OrderItem;
import com.yukthi.persistence.ForeignConstraintViolationException;
import com.yukthi.persistence.ICrudRepository;
import com.yukthi.persistence.UniqueConstraintViolationException;
import com.yukthi.persistence.repository.RepositoryFactory;

import junit.framework.Assert;

/**
 * @author akiran
 *
 */
public class TConstraintExceptionHandling extends TestSuiteBase
{
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
