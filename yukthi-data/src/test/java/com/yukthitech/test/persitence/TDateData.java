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
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.test.persitence.entity.DateDataEntity;
import com.yukthitech.test.persitence.entity.IDateDataRepository;

/**
 * Test cases to test basic CRUD functionality
 * @author akiran
 */
public class TDateData extends TestSuiteBase
{
	@AfterMethod
	public void cleanup(ITestResult result)
	{
		Object params[] = result.getParameters();
		RepositoryFactory factory = (RepositoryFactory)params[0];
		
		//cleanup the emp table
		factory.dropRepository(DateDataEntity.class);
	}
	
	private Date date(int days)
	{
		return DateUtils.addDays(new Date(), days);
	}

	/**
	 * Tests the update functionality
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testForUpdate(RepositoryFactory factory)
	{
		IDateDataRepository dateRepository = factory.getRepository(IDateDataRepository.class);

		DateDataEntity data1 = new DateDataEntity("test1", date(-100), date(-1000));
		dateRepository.save(data1);
		
		DateDataEntity data2 = new DateDataEntity("test2", date(-200), date(-2000));
		dateRepository.save(data2);
		
		//update the emp with different emp id and email id
		DateDataEntity data3 = new DateDataEntity("test1", date(-101), date(-1001));
		data3.setId(data1.getId());
		Assert.assertTrue(dateRepository.update(data3));
		
		DateDataEntity updatedData = dateRepository.findById(data1.getId());
		Assert.assertEquals("test1", updatedData.getName());
		Assert.assertEquals(DateUtils.truncate(date(-101), Calendar.DATE), DateUtils.truncate(updatedData.getDate(), Calendar.DATE));
		Assert.assertEquals(DateUtils.truncate(date(-1001), Calendar.HOUR), DateUtils.truncate(updatedData.getDateWithTime(), Calendar.HOUR));
	}

	/**
	 * Tests finder functionality
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testFinders(RepositoryFactory factory)
	{
		IDateDataRepository dateRepository = factory.getRepository(IDateDataRepository.class);
		
		DateDataEntity data1 = new DateDataEntity("test1", date(-100), date(-1000));
		dateRepository.save(data1);
		
		DateDataEntity data2 = new DateDataEntity("test2", date(-200), date(-2000));
		dateRepository.save(data2);

		List<DateDataEntity> resLst = dateRepository.fetchDataAfter(date(-150));
		Assert.assertEquals(resLst.size(), 1);
		Assert.assertEquals(resLst.get(0).getName(), "test1");
		
		resLst = dateRepository.fetchDataBefore(date(-1500));
		Assert.assertEquals(resLst.size(), 1);
		Assert.assertEquals(resLst.get(0).getName(), "test2");
	}
}
