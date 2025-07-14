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

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.test.persitence.entity.Employee;
import com.yukthitech.test.persitence.entity.Employee1;
import com.yukthitech.test.persitence.entity.IEmployee1Repository;
import com.yukthitech.test.persitence.entity.IEmployeeRepository;

/**
 * Test cases to test basic CRUD functionality
 * @author akiran
 */
public class TTransactions extends TestSuiteBase
{
	@AfterMethod
	public void cleanup(ITestResult result)
	{
		Object params[] = result.getParameters();
		RepositoryFactory factory = (RepositoryFactory)params[0];
		
		//cleanup the emp table
		factory.dropRepository(Employee.class);
		factory.dropRepository(Employee1.class);
	}
	
	@Test(dataProvider = "repositoryFactories")
	public void testExecuteInTrans_rollback(RepositoryFactory factory)
	{
		IEmployeeRepository empRepository = factory.getRepository(IEmployeeRepository.class);
		IEmployee1Repository emp1Repository = factory.getRepository(IEmployee1Repository.class);

		empRepository.deleteAll();
		emp1Repository.deleteAll();
		
		try
		{
			empRepository.executeInTransaction(true, () -> 
			{
				Employee emp = new Employee("12345", "kranthi@kk.com", "kranthi", "90232333", 28);
				empRepository.save(emp);
				
				Employee1 emp1 = new Employee1("123456", "kranthi@kk.com", "kranthi", "90232333", 28);
				emp1Repository.save(emp1);
	
				Employee1 emp2 = new Employee1("123457", "kranthi@kk.com", "kranthi", "90232334", 28);
				emp1Repository.save(emp2);
			});
			
			Assert.fail("No exception is thrown");
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		
		Assert.assertEquals(empRepository.getCount(), 0L);
		Assert.assertEquals(emp1Repository.getCount(), 0L);
	}

	
	@Test(dataProvider = "repositoryFactories")
	public void testExecuteInTrans_commit(RepositoryFactory factory)
	{
		IEmployeeRepository empRepository = factory.getRepository(IEmployeeRepository.class);
		IEmployee1Repository emp1Repository = factory.getRepository(IEmployee1Repository.class);

		empRepository.deleteAll();
		emp1Repository.deleteAll();
		
		empRepository.executeInTransaction(true, () -> 
		{
			Employee emp = new Employee("12345", "kranthi@kk.com", "kranthi", "90232333", 28);
			empRepository.save(emp);
			
			Employee1 emp1 = new Employee1("123456", "kranthi1@kk.com", "kranthi", "90232333", 28);
			emp1Repository.save(emp1);

			Employee1 emp2 = new Employee1("123457", "kranthi2@kk.com", "kranthi", "90232334", 28);
			emp1Repository.save(emp2);
		});
		
		Assert.assertEquals(empRepository.getCount(), 1L);
		Assert.assertEquals(emp1Repository.getCount(), 2L);
	}
}
