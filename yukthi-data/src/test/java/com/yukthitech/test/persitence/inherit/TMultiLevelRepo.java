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

package com.fw.test.persitence.inherit;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.test.persitence.TestSuiteBase;
import com.yukthitech.test.persitence.entity.Employee;

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
