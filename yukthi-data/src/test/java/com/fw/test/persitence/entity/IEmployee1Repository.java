package com.fw.test.persitence.entity;

import com.yukthi.persistence.ICrudRepository;
import com.yukthi.persistence.repository.annotations.Field;

public interface IEmployee1Repository extends ICrudRepository<Employee1>
{
	@Field("address")
	public Address fetchAddressById(long id);
}
