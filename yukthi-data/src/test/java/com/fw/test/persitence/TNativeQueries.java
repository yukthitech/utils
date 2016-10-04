/*
 * The MIT License (MIT)
 * Copyright (c) 2016 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.fw.test.persitence;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fw.test.persitence.entity.Employee;
import com.fw.test.persitence.entity.IEmployeeRepository;
import com.fw.test.persitence.queries.EmpSearchQuery;
import com.yukthi.persistence.IDataStore;
import com.yukthi.persistence.NativeQueryFactory;
import com.yukthi.persistence.repository.RepositoryFactory;
import com.yukthi.utils.CommonUtils;

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
		
		cleanFactoryAfterClass(factory);
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		repo.save(new Employee("1230", "user0@test.com", "user1", "1234560", 20));
		repo.save(new Employee("1231", "user1@test.com", "user2", "1234561", 25));
		repo.save(new Employee("1232", "user2@test.com", "user3", "1234564", 50));
		
		repo.save(new Employee("2230", "test0@test.com", "test1", "1234565", 20));
		repo.save(new Employee("2231", "test1@test.com", "test2", "1234566", 25));
	}
	
	@Override
	protected void cleanFactoryAfterClass(RepositoryFactory factory)
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
}
