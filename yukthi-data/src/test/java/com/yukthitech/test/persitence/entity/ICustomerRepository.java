package com.yukthitech.test.persitence.entity;

import com.yukthitech.persistence.ICrudRepository;

public interface ICustomerRepository extends ICrudRepository<Customer>
{
	public Customer findByName(String name);
}
