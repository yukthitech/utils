package com.fw.test.persitence.entity;

import java.util.List;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.AggregateFunction;
import com.yukthitech.persistence.repository.annotations.AggregateFunctionType;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Field;

public interface IOrderRepository extends ICrudRepository<Order>
{
	public List<Order> findOrdersWithItem(@Condition("items.itemName") String itemName);
	
	public List<Order> findOrdersOfCusomer(@Condition("customer.name") String customerName);
	
	public List<Order> findOrdersOfCusomerGroup(@Condition("customer.customerGroups.name") String customerGroupName);

	@Field("customer.name")
	public String findCustomerName(@Condition("orderNo") int orderNo);
	
	public Order findOrderByOrderNo(int orderNo);
	
	@AggregateFunction(type = AggregateFunctionType.COUNT)
	public int getOrderCount(@Condition("customer.name") String customerName);

	@AggregateFunction(type = AggregateFunctionType.MAX, field = "cost")
	public float getMaxCost(@Condition("customer.name") String customerName);

	@AggregateFunction(type = AggregateFunctionType.AVG, field = "cost")
	public float getAverageCost(@Condition("customer.name") String customerName);

	@AggregateFunction(type = AggregateFunctionType.MIN, field = "cost")
	public float getMinCost(@Condition("customer.name") String customerName);
}
