package com.fw.test.persitence.entity;

import java.util.List;

import com.yukthi.persistence.ICrudRepository;
import com.yukthi.persistence.repository.annotations.Condition;
import com.yukthi.persistence.repository.annotations.Field;

public interface ICustomerGroupRepository extends ICrudRepository<CustomerGroup>
{
	public List<CustomerGroup> findGroupsOfCusomer(@Condition("customers.name") String customerName);
	
	@Field("customers.orders.title")
	public List<String> findOrderTitles(@Condition("name") String groupName);
}
