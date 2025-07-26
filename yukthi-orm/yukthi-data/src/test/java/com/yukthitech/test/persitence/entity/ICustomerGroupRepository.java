/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.test.persitence.entity;

import java.util.List;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.persistence.repository.annotations.RelationUpdateType;

public interface ICustomerGroupRepository extends ICrudRepository<CustomerGroup>
{
	public List<CustomerGroup> findGroupsOfCusomer(@Condition("customers.name") String customerName);
	
	@Field("customers.orders.title")
	public List<String> findOrderTitles(@Condition("name") String groupName);

	public CustomerGroup findByName(String name);

	public int updateCustomers(
			@Condition("name") String name, 
			@Field(value = "customers", relationUpdate = RelationUpdateType.SYNC_RELATION)
			List<Customer> customers);

	public int updateFullCustomers(
			@Condition("name") String name, 
			@Field(value = "customers", relationUpdate = RelationUpdateType.CASCADE)
			List<Customer> customers);
}
