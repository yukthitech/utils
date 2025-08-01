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
package com.yukthitech.test.persitence;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.test.persitence.entity.Employee;
import com.yukthitech.test.persitence.entity.IEmployeeRepository;
import com.yukthitech.test.persitence.queries.EmpSearchQuery;
import com.yukthitech.persistence.FilterAction;
import com.yukthitech.persistence.IDataFilter;
import com.yukthitech.persistence.IDataStore;
import com.yukthitech.persistence.NativeQueryFactory;
import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.utils.CommonUtils;

/**
 * 
 * @author akiran
 */
public class TNativeQueries extends TestSuiteBase
{
	@Override
	protected void initFactoryBeforeClass(RepositoryFactory factory)
	{
		NativeQueryFactory nativeQueryFactory = new NativeQueryFactory();
		nativeQueryFactory.addResource("/test-native-queries.xml");
		
		IDataStore dataStore = factory.getDataStore();
		dataStore.setNativeQueryFactory(nativeQueryFactory);
		
		cleanFactory(factory);
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		repo.save(new Employee("1230", "user0@test.com", "user1", "1234560", 20));
		repo.save(new Employee("1231", "user1@test.com", "user2", "1234561", 25));
		repo.save(new Employee("1232", "user2@test.com", "user3", "1234564", 50));
		
		repo.save(new Employee("2230", "test0@test.com", "test1", "1234565", 20));
		repo.save(new Employee("2231", "test1@test.com", "test2", "1234566", 25));
	}
	
	@Override
	protected void cleanFactory(RepositoryFactory factory)
	{
		//cleanup the emp table
		factory.dropRepository(Employee.class);
	}

	@Test(dataProvider = "repositoryFactories")
	public void testNativeInsert(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);

		boolean res = repo.addEmployee(new Employee("4321", "ntest@test.com", "ntest1", "64532113", 43));
		Assert.assertTrue(res);

		Employee fromDb = repo.findByEmployeeNo("4321");
		Assert.assertEquals(fromDb.getAge(), 43);
		Assert.assertEquals(fromDb.getEmailId(), "ntest@test.com");
		Assert.assertEquals(fromDb.getName(), "ntest1");
		Assert.assertEquals(fromDb.getPhoneNo(), "64532113");
	}

	@Test(dataProvider = "repositoryFactories")
	public void testNativeUpdate(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);

		Employee empRead = repo.findByEmployeeNo("1230");
		
		int res = repo.updateEmployee(CommonUtils.toMap(
				"id", empRead.getId(),
				"emailId", "nupdate@test.com"
		));
		
		Assert.assertEquals(res, 1);
		
		empRead = repo.findByEmployeeNo("1230");
		Assert.assertEquals(empRead.getEmailId(), "nupdate@test.com");
	}

	@Test(dataProvider = "repositoryFactories")
	public void testNativeDelete(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);

		int res = repo.deleteEmployee(CommonUtils.toMap(
				"name", "test"
		));
		
		Assert.assertTrue(res > 1);
		
		Employee empRead = repo.findByEmployeeNo("2230");
		Assert.assertNull(empRead);
	}

	@Test(dataProvider = "repositoryFactories")
	public void testNativeSelect(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);

		Employee e = repo.readEmployee1(new EmpSearchQuery("user1", null, null, null));
		Assert.assertEquals(e.getEmployeeNo(), "1230");
		Assert.assertEquals(e.getEmailId(), "user0@test.com");
		Assert.assertEquals(e.getPhoneNo(), "1234560");
		Assert.assertEquals(e.getAge(), 20);
		
		int count = repo.fetchCount(new EmpSearchQuery("user1", null, null, null));
		Assert.assertEquals(count, 1);
		
		Long count1 = repo.fetchCount1(new EmpSearchQuery("user1", null, null, null));
		Assert.assertEquals((long)count1, 1L);
	}

	@Test(dataProvider = "repositoryFactories")
	public void testNativeSelectMulti(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);

		List<Employee> lst = repo.readEmployee2(new EmpSearchQuery("user", null, 15, 30));
		Assert.assertEquals(lst.size(), 2);
		
		Employee e = lst.get(0);
		Assert.assertEquals(e.getEmployeeNo(), "1230");
		Assert.assertEquals(e.getName(), "user1");
		Assert.assertEquals(e.getEmailId(), "user0@test.com");
		Assert.assertEquals(e.getPhoneNo(), "1234560");
		Assert.assertEquals(e.getAge(), 20);
		
		e = lst.get(1);
		Assert.assertEquals(e.getEmployeeNo(), "1231");
		Assert.assertEquals(e.getName(), "user2");
		Assert.assertEquals(e.getEmailId(), "user1@test.com");
		Assert.assertEquals(e.getPhoneNo(), "1234561");
		Assert.assertEquals(e.getAge(), 25);
	}
	
	
	@Test(dataProvider = "repositoryFactories")
	public void testNativeSelectWithFilter(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		AtomicInteger recordCount = new AtomicInteger(0);
		
		List<Employee> lst = repo.readEmployeeWithFilter(new EmpSearchQuery("user", null, null, null), new IDataFilter<Employee>()
		{
			@Override
			public FilterAction filter(Employee data)
			{
				recordCount.incrementAndGet();
				return recordCount.get() <= 2 ? FilterAction.ACCEPT : FilterAction.REJECT_AND_STOP;
			}
		});
		
		Assert.assertEquals(lst.size(), 2);
		Assert.assertTrue(lst.get(0).getEmailId().startsWith("user"));
		Assert.assertTrue(lst.get(1).getEmailId().startsWith("user"));
	}
}
