package com.yukthitech.test.persitence.entity;

import java.util.List;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Field;

public interface ICustomerGroupRepository extends ICrudRepository<CustomerGroup>
{
	public List<CustomerGroup> findGroupsOfCusomer(@Condition("customers.name") String customerName);
	
	@Field("customers.orders.title")
	public List<String> findOrderTitles(@Condition("name") String groupName);

	public CustomerGroup findByName(String name);
}
