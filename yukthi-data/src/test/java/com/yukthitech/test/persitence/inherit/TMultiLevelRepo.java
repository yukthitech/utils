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
package com.yukthitech.test.persitence.inherit;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.test.persitence.TestSuiteBase;
import com.yukthitech.test.persitence.entity.Employee;
import com.yukthitech.persistence.repository.RepositoryFactory;

/**
 * @author akiran
 *
 */
public class TMultiLevelRepo extends TestSuiteBase
{
	@Override
	protected void initFactoryBeforeClass(RepositoryFactory factory)
	{
		cleanFactoryAfterClass(factory);
		IEmpRepository repo = factory.getRepository(IEmpRepository.class);
		repo.save(new Employee("1230", "user0@test.com", "user1", "1234560", 20));
		repo.save(new Employee("1231", "user1@test.com", "user2", "1234561", 25));
	}

	@Override
	protected void cleanFactoryAfterClass(RepositoryFactory factory)
	{
		//cleanup the emp table
		factory.dropRepository(Employee.class);
	}
	
	@Test(dataProvider = "repositoryFactories")
	public void testExtendedRepo(RepositoryFactory factory)
	{
		IEmpRepository repo = factory.getRepository(IEmpRepository.class);

		//ensure methods defined at mid level are working
		Employee e = repo.findByName("user1");
		
		Assert.assertNotNull(e);
		Assert.assertEquals(e.getEmployeeNo(), "1230");

		//ensure methods defined at last level are working
		e = repo.fetchByEmpNo("1231");
		Assert.assertNotNull(e);
		Assert.assertEquals(e.getName(), "user2");
		
		//ensure collection method is working properly
		List<Employee> empLst = repo.findByNamePattern("user%");
		Assert.assertNotNull(empLst);
		Assert.assertEquals(empLst.size(), 2);
	}

}
