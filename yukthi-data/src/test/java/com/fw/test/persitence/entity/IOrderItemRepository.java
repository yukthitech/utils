package com.fw.test.persitence.entity;

import com.yukthi.persistence.ICrudRepository;
import com.yukthi.persistence.repository.annotations.Condition;
import com.yukthi.persistence.repository.annotations.Field;
import com.yukthi.persistence.repository.annotations.UpdateOperator;

public interface IOrderItemRepository extends ICrudRepository<OrderItem>
{
	public boolean updateForOrder(OrderItem orderItem, @Condition("order.id") Long orderId, @Condition("itemName") String name);
	
	public boolean updateQuantityForOrder(@Condition("order.id") Long orderId, @Condition("itemName") String name, @Field("quantity") int quantity);
	
	public int deleteByOrderName(@Condition("quantity") int quantity, @Condition("order.title") String orderName);
	
	public int deleteByCustomerName(@Condition("quantity") int quantity, @Condition("order.customer.name") String customerName);
	
	public int updateByCustomerName(@Field(value = "quantity", updateOp = UpdateOperator.ADD) int quantity, @Condition("order.customer.name") String customerName);
}
