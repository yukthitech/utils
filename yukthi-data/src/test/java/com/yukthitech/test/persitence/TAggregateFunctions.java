package com.yukthitech.test.persitence;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.test.persitence.entity.Customer;
import com.yukthitech.test.persitence.entity.CustomerGroup;
import com.yukthitech.test.persitence.entity.IOrderItemRepository;
import com.yukthitech.test.persitence.entity.IOrderRepository;
import com.yukthitech.test.persitence.entity.Order;
import com.yukthitech.test.persitence.entity.OrderItem;
import com.yukthitech.persistence.GenericRepository;
import com.yukthitech.persistence.repository.RepositoryFactory;

/**
 * Test cases for aggregate functions.
 * @author akiran
 */
public class TAggregateFunctions extends TestSuiteBase
{
	@Override
	protected void initFactoryBeforeClass(RepositoryFactory factory)
	{
		//this ensures all child classes are created as required before insert is started
		factory.getRepository(IOrderItemRepository.class);
		
		GenericRepository genericRepository = new GenericRepository(factory);

		Order order1 = new Order("order1", 10, null, 100);
		Order order2 = new Order("order2", 12, null, 200);
		Order order3 = new Order("order3", 13, null, 300);
		
		Order order4 = new Order("order4", 20, null, 300);
		Order order5 = new Order("order5", 30, null, 400);
		
		Customer customer1 = new Customer("CustomerAgr1", null, Arrays.asList(order1, order2, order3));
		Customer customer2 = new Customer("CustomerAgr2", null, Arrays.asList(order4, order5));
		
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
	
	@Test(dataProvider = "repositoryFactories")
	public void testSum(RepositoryFactory factory)
	{
		IOrderRepository repo = factory.getRepository(IOrderRepository.class);
		
		Assert.assertEquals(repo.getOrderCount("CustomerAgr1"), 3);
		
		Assert.assertEquals(repo.getMaxCost("CustomerAgr2"), 400f);
		
		Assert.assertEquals(repo.getAverageCost("CustomerAgr1"), 200f);
		
		Assert.assertEquals(repo.getMinCost("CustomerAgr1"), 100f);
	}

}
