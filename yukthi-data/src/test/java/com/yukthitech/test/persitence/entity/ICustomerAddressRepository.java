package com.yukthitech.test.persitence.entity;

import com.yukthitech.persistence.ICrudRepository;

public interface ICustomerAddressRepository extends ICrudRepository<CustomerAddress>
{
	public CustomerAddress findByPropertyId(String propId);
}
