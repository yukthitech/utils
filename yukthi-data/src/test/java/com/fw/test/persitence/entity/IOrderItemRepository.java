package com.fw.test.persitence.entity;

import com.yukthi.persistence.ICrudRepository;
import com.yukthi.persistence.repository.annotations.Condition;
import com.yukthi.persistence.repository.annotations.Field;

public interface IOrderItemRepository extends ICrudRepository<OrderItem>
{
	public boolean updateForOrder(OrderItem orderItem, @Condition("order.id") Long orderId, @Condition("itemName") String name);
	
	public boolean updateQuantityForOrder(@Condition("order.id") Long orderId, @Condition("itemName") String name, @Field("quantity") int quantity);
}
