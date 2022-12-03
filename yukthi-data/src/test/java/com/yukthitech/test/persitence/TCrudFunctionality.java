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

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.test.persitence.entity.Address;
import com.yukthitech.test.persitence.entity.Employee;
import com.yukthitech.test.persitence.entity.Employee1;
import com.yukthitech.test.persitence.entity.IEmployee1Repository;
import com.yukthitech.test.persitence.entity.IEmployeeRepository;

/**
 * Test cases to test basic CRUD functionality
 * @author akiran
 */
public class TCrudFunctionality extends TestSuiteBase
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
	

	/**
	 * Tests the update functionality
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testForUpdate(RepositoryFactory factory)
	{
		IEmployeeRepository empRepository = factory.getRepository(IEmployeeRepository.class);
		
		Employee emp = new Employee("12345", "kranthi@kk.com", "kranthi", "90232333", 28);
		empRepository.save(emp);
		
		Employee emp1 = new Employee("123452", "kiran@kk.com", "kiran", "90232333", 28);
		empRepository.save(emp1);
		
		//update the emp with different emp id and email id
		Employee empForUpdate = new Employee("12345", "kranthi123@kk.com", "kranthi12", "12390232333", 28);
		empForUpdate.setId(emp.getId());
		Assert.assertTrue(empRepository.update(empForUpdate));
		
		Employee updatedEmp = empRepository.findById(emp.getId());
		Assert.assertEquals("12345", updatedEmp.getEmployeeNo()); //check emp no is not changed
		Assert.assertEquals("kranthi123@kk.com", updatedEmp.getEmailId());
		Assert.assertEquals("kranthi12", updatedEmp.getName());
		Assert.assertEquals("12390232333", updatedEmp.getPhoneNo());

		//cleanup the emp table
		factory.dropRepository(Employee.class);
	}

	/**
	 * Tests unique constraint working during update
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testUniquenessDuringUpdate(RepositoryFactory factory)
	{
		IEmployeeRepository empRepository = factory.getRepository(IEmployeeRepository.class);
		
		Employee emp = new Employee("12345", "kranthi@kk.com", "kranthi", "90232333", 28);
		empRepository.save(emp);
		
		Employee emp1 = new Employee("123452", "kiran@kk.com", "kiran", "90232333", 28);
		empRepository.save(emp1);

		try
		{
			Employee empForUpdate = new Employee("1234523", "kiran@kk.com", "kranthi12", "12390232333", 28);
			empForUpdate.setId(emp.getId());
			
			empRepository.update(empForUpdate);
			Assert.fail("Employee got updated with duplicate mail");
		}catch(Exception ex)
		{
		}
		
		//cleanup the emp table
		factory.dropRepository(Employee.class);
	}

	/**
	 * Tests finder functionality
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testFinders(RepositoryFactory factory)
	{
		IEmployeeRepository empRepository = factory.getRepository(IEmployeeRepository.class);
		
		Employee emp = new Employee("12345", "kranthi@kk.com", "kranthi", "90232333", 28);
		empRepository.save(emp);
		long empId = emp.getId();
		
		Employee emp1 = new Employee("123452", "kiran@kk.com", "kiran", "90232333", 28);
		empRepository.save(emp1);
		long empId1 = emp1.getId();

		Employee emp2 = new Employee("123455", "abc@kk.com", "abc", "887788778", 28);
		empRepository.save(emp2);

		Employee foundEmployee = empRepository.findById(empId1);
		Assert.assertEquals(emp1.getEmailId(), foundEmployee.getEmailId());
		
		foundEmployee = empRepository.findByEmployeeNo("12345");
		Assert.assertEquals(empId, foundEmployee.getId());
		
		Assert.assertEquals(empId, empRepository.findIdByEmail("kranthi@kk.com"));
		Assert.assertEquals("kiran@kk.com", empRepository.findEmailByEmployeeNo("123452"));
		
		Assert.assertEquals(2, empRepository.findByPhoneNo("%90%").size());
		
		//cleanup the emp table
		factory.dropRepository(Employee.class);
	}
	
	/**
	 * Tests Delete functionality
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testDelete(RepositoryFactory factory)
	{
		IEmployeeRepository empRepository = factory.getRepository(IEmployeeRepository.class);
		
		Employee emp = new Employee("12345", "kranthi@kk.com", "kranthi", "90232333", 28);
		empRepository.save(emp);
		
		Employee emp1 = new Employee("123452", "kiran@kk.com", "kiran", "90232333", 28);
		empRepository.save(emp1);

		//ensure two records are present
		Assert.assertEquals(empRepository.getCount(), 2);
		
		//delete first entity
		empRepository.deleteById(emp.getId());
		
		//ensure proper count is found
		Assert.assertEquals(empRepository.getCount(), 1);
		
		//ensure proper entity can be fetched
		Assert.assertEquals(empRepository.findById(emp1.getId()).getEmailId(), "kiran@kk.com");
		
		//cleanup the emp table
		factory.dropRepository(Employee.class);
	}
	
	@Test(dataProvider = "repositoryFactories")
	public void testComplexObjectSave(RepositoryFactory factory)
	{
		IEmployee1Repository empRepository = factory.getRepository(IEmployee1Repository.class);
		
		Date dob = DateUtils.addDays(new Date(), -1000);
		
		Employee1 emp = new Employee1("12345", "kranthi@kk.com", "kranthi", "90232333", 28);
		emp.setAddress(new Address("city", "state"));
		emp.setMarried(true);
		emp.setDob(dob);
		empRepository.save(emp);
		
		Employee1 savedEmp = empRepository.findById(emp.getId());
		Assert.assertNotNull(emp.getAddress());
		Assert.assertEquals(savedEmp.getAddress().getCity(), "city");
		Assert.assertEquals(savedEmp.getAddress().getState(), "state");
		Assert.assertTrue(savedEmp.isMarried());
		
		Assert.assertEquals(DateUtils.truncate(savedEmp.getDob(), Calendar.MINUTE), DateUtils.truncate(dob, Calendar.MINUTE));
		
		//try to fetch complex field directly
		Address resultAddress = empRepository.fetchAddressById(emp.getId());
		Assert.assertNotNull(resultAddress);
		Assert.assertEquals(resultAddress.getCity(), "city");
		Assert.assertEquals(resultAddress.getState(), "state");
	}	
	
	/*
	@Test
	public void testDelete()
	{
		IEmployeeRepository empRepository = factory.getRepository(IEmployeeRepository.class);
		IAddressRepository addressRepository = factory.getRepository(IAddressRepository.class);
		
		Employee emp = new Employee("12345", "kranthi@kk.com", "kranthi", "90232333");
		empRepository.save(emp);
		
		Employee emp1 = new Employee("123452", "kiran@kk.com", "kiran", "90232333");
		empRepository.save(emp1);

		addressRepository.save(new Address(null, "Address1", emp.getId(), Address.PARENT_TYPE_EMPLOYEE));
		addressRepository.save(new Address(null, "Address2", emp.getId(), Address.PARENT_TYPE_EMPLOYEE));
		
		addressRepository.save(new Address(null, "Address3", emp1.getId(), Address.PARENT_TYPE_EMPLOYEE));
		addressRepository.save(new Address(null, "Address4", emp1.getId(), Address.PARENT_TYPE_EMPLOYEE));
		
		Assert.assertEquals(2, addressRepository.findByParentId(emp.getId()).size(), "Addresses are not added properly");
		
		String invalidId = "3455566";
		
		try
		{
			//provide invalid id
			addressRepository.save(new Address(null, "Address3", invalidId, Address.PARENT_TYPE_EMPLOYEE));
			Assert.fail("Address got saved with invalid id");
		}catch(ForeignConstraintViolationException ex)
		{
			Assert.assertEquals("PARENT_ID", ex.getConstraintName());
			Assert.assertTrue(ex.getMessage().contains(invalidId));
		}
		
		addressRepository.save(new Address(null, "Address5", "2345678", Address.PARENT_TYPE_OTHER));
		addressRepository.save(new Address(null, "Address5", "2345556", Address.PARENT_TYPE_OTHER));
		
		//Delete parent employee without deleting child addresses, that should throw error
		try
		{
			empRepository.deleteById(emp.getId());
			Assert.fail("Employee got deleted when child items (addresses) are still present");
		}catch(ChildConstraintViolationException ex)
		{
			Assert.assertEquals("PARENT_ID", ex.getConstraintName());
			Assert.assertTrue(ex.getMessage().contains(Address.class.getName()));
		}

		//delete child address items and then try to delete parent employee, which should succeed
		addressRepository.deleteByParentId(emp.getId());
		
		Assert.assertEquals(0, addressRepository.findByParentId(emp.getId()).size(), "Addresses are not delete properly");
		
		empRepository.deleteById(emp.getId());
	}

	/*
	@Test
	public void testAutoChildDelete()
	{
		IEmployeeRepository empRepository = factory.getRepository(IEmployeeRepository.class);
		IAddress1Repository address1Repository = factory.getRepository(IAddress1Repository.class);
		
		Employee emp = new Employee("12345", "kranthi@kk.com", "kranthi", "90232333");
		empRepository.save(emp);
		
		Employee emp1 = new Employee("123452", "kiran@kk.com", "kiran", "90232333");
		empRepository.save(emp1);

		address1Repository.save(new Address1(null, "Address1", emp.getId(), Address.PARENT_TYPE_EMPLOYEE));
		address1Repository.save(new Address1(null, "Address2", emp.getId(), Address.PARENT_TYPE_EMPLOYEE));
		
		address1Repository.save(new Address1(null, "Address3", emp1.getId(), Address.PARENT_TYPE_EMPLOYEE));
		address1Repository.save(new Address1(null, "Address4", emp1.getId(), Address.PARENT_TYPE_EMPLOYEE));
		
		Assert.assertEquals(2, address1Repository.findByParentId(emp.getId()).size(), "Addresses are not added properly");
		Assert.assertEquals(2, address1Repository.findByParentId(emp1.getId()).size(), "Addresses are not added properly");
		
		//Delete parent employee without deleting child addresses, which should delete employee along with child addresses
		empRepository.deleteById(emp.getId());
		
		Assert.assertEquals(0, address1Repository.findByParentId(emp.getId()).size(), "Child addresses are not deleted properly");
		Assert.assertEquals(2, address1Repository.findByParentId(emp1.getId()).size(), "Other addresses got deleted by bug");
	}
	*/
}
