package com.fw.test.persitence.entity;

import java.util.List;

import com.yukthi.persistence.ICrudRepository;
import com.yukthi.persistence.repository.annotations.Condition;

public interface ICustomerGroupRepository extends ICrudRepository<CustomerGroup>
{
	public List<CustomerGroup> findGroupsOfCusomer(@Condition("customers.name") String customerName);
}
