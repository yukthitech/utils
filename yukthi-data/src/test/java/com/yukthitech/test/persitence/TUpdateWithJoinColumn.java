package com.fw.test.persitence;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.test.persitence.entity.IOrderItemRepository;
import com.yukthitech.test.persitence.entity.IOrderRepository;
import com.yukthitech.test.persitence.entity.OrderItem;

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
