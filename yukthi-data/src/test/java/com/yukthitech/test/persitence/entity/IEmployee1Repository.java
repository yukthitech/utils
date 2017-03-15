package com.yukthitech.test.persitence.entity;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.Field;

public interface IEmployee1Repository extends ICrudRepository<Employee1>
{
	@Field("address")
	public Address fetchAddressById(long id);
}
